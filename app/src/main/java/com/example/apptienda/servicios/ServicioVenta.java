package com.example.apptienda.servicios;

import com.example.apptienda.data.DatabaseHelper;
import com.example.apptienda.data.VentaRepository;
import com.example.apptienda.core.repositories.SaleRepository;
import com.example.apptienda.modelos.Producto;
import com.example.apptienda.modelos.modeloVenta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Servicio para manejar la lógica de negocio de ventas (capa de aplicación).
 *
 * Responsabilidad:
 * - Contiene validaciones y reglas de negocio asociadas a las ventas (stock, precios, códigos,
 *   generación de boletas, cálculos de totales).
 * - Depende de la abstracción `SaleRepository` (paquete `core.repositories`) para acceder a
 *   la persistencia. De esta forma la lógica queda desacoplada de la implementación concreta
 *   de la base de datos y puede ser reutilizada por otra UI (por ejemplo un frontend Flutter).
 *
 * Conexiones principales:
 * - `ServicioProducto`: usado para consultar producto y validar stock/precios.
 * - `SaleRepository` (por ejemplo `VentaRepository` en `data/`) para operaciones CRUD de ventas.
 *
 * Nota sobre compatibilidad:
 * - Se mantiene un constructor de compatibilidad que recibe `DatabaseHelper` y construye
 *   internamente el `VentaRepository` existente para no romper llamadas actuales.
 */
public class ServicioVenta {
	private final SaleRepository ventaRepository;
	private final ServicioProducto servicioProducto;

	/**
	 * Constructor principal: inyecta una implementación de `SaleRepository`.
	 * - Recomendado para tests y para integrar con adaptadores distintos a la BD local.
	 */
	public ServicioVenta(SaleRepository ventaRepository, ServicioProducto servicioProducto) {
		this.ventaRepository = ventaRepository;
		this.servicioProducto = servicioProducto;
	}

	/**
	 * Constructor de compatibilidad: mantiene la posibilidad de pasar `DatabaseHelper` tal como
	 * se hacía históricamente. Internamente construye `VentaRepository`.
	 */
	public ServicioVenta(DatabaseHelper dbHelper, ServicioProducto servicioProducto) {
		this(new VentaRepository(dbHelper), servicioProducto);
	}

	/**
	 * Registra una venta aplicando las validaciones de negocio:
	 * - Verifica si la venta hace referencia a un producto existente y suficiente stock.
	 * - Completa precios por defecto si vienen vacíos y recalcula totales (IGV, total).
	 * - Devuelve el objeto venta persistido por el repositorio.
	 *
	 * Lanza Exception en caso de validaciones que impidan registrar la venta.
	 */
	public modeloVenta registrarVenta(modeloVenta venta) throws Exception {
		if (venta == null) {
			throw new IllegalArgumentException("Venta requerida");
		}

		if (venta.tieneProductoRegistrado()) {
			Producto producto = servicioProducto.obtenerProductoPorId(venta.getProductoId());
			if (producto == null) {
				throw new Exception("Producto no encontrado");
			}
			if (!producto.tieneStockSuficiente(venta.getCantidadParaStock())) {
				throw new Exception("Stock insuficiente");
			}
			if (venta.getPrecioUnitario() <= 0) {
				venta.setPrecioUnitario(producto.getPrecioVenta());
			}
			if (venta.getCostoUnitario() <= 0) {
				venta.setCostoUnitario(producto.getPrecioCompra());
			}
			if (venta.getNombreProductoManual() == null || venta.getNombreProductoManual().isEmpty()) {
				venta.setNombreProductoManual(producto.getNombre());
			}
		} else {
			if (venta.getNombreProductoManual() == null || venta.getNombreProductoManual().isEmpty()) {
				throw new Exception("Debe ingresar el nombre del producto a vender");
			}
			if (venta.getCostoUnitario() <= 0) {
				throw new Exception("Debe ingresar el costo por unidad para productos nuevos");
			}
		}

		venta.recalcularTotales();
		return ventaRepository.registrar(venta);
	}

	/** Lista todas las ventas almacenadas (delegado al repositorio). */
	public List<modeloVenta> listarVentas() {
		return ventaRepository.listar();
	}

	/** Elimina una venta por id (delegado al repositorio). */
	public boolean eliminarVenta(long ventaId) {
		return ventaRepository.eliminar(ventaId);
	}

	/** Obtiene una venta por su código único. */
	public modeloVenta obtenerPorCodigo(String codigo) {
		return ventaRepository.obtenerPorCodigo(codigo);
	}

	/** Genera un nuevo código para venta (delegado al repositorio). */
	public String generarCodigoVenta() {
		return ventaRepository.generarCodigo();
	}

	/**
	 * Filtra las ventas por una fecha específica (mismo día).
	 * - Devuelve una lista vacía si la fecha es null.
	 */
	public List<modeloVenta> obtenerVentasPorFecha(Date fecha) {
		List<modeloVenta> resultado = new ArrayList<>();
		if (fecha == null) {
			return resultado;
		}
		Calendar target = Calendar.getInstance();
		target.setTime(fecha);
		for (modeloVenta venta : listarVentas()) {
			Calendar actual = Calendar.getInstance();
			actual.setTime(venta.getFecha());
			if (target.get(Calendar.YEAR) == actual.get(Calendar.YEAR)
					&& target.get(Calendar.DAY_OF_YEAR) == actual.get(Calendar.DAY_OF_YEAR)) {
				resultado.add(venta);
			}
		}
		return resultado;
	}

	/** Calcula la suma de totales de las ventas del día actual. */
	public double calcularVentasDelDia() {
		List<modeloVenta> ventasHoy = obtenerVentasPorFecha(new Date());
		double total = 0;
		for (modeloVenta venta : ventasHoy) {
			total += venta.getTotal();
		}
		return total;
	}

	/** Devuelve las ventas ocurridas en el mes actual. */
	public List<modeloVenta> obtenerVentasDelMes() {
		List<modeloVenta> resultado = new ArrayList<>();
		Calendar hoy = Calendar.getInstance();
		int mes = hoy.get(Calendar.MONTH);
		int anio = hoy.get(Calendar.YEAR);
		for (modeloVenta venta : listarVentas()) {
			Calendar actual = Calendar.getInstance();
			actual.setTime(venta.getFecha());
			if (actual.get(Calendar.YEAR) == anio && actual.get(Calendar.MONTH) == mes) {
				resultado.add(venta);
			}
		}
		return resultado;
	}

	/**
	 * Genera una representación en texto simple de una boleta de venta con los campos
	 * requeridos (codigo, fecha, dni, producto, cantidades, precios, IGV, total).
	 * - Esta función solo formatea y no persiste nada.
	 */
	public String generarBoleta(modeloVenta venta) {
		if (venta == null) {
			return "Venta no encontrada";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
		StringBuilder builder = new StringBuilder();
		builder.append("BOLETA DE VENTA\n");
		builder.append("Codigo: ").append(venta.getCodigo()).append('\n');
		builder.append("Fecha: ").append(sdf.format(venta.getFecha())).append('\n');
		builder.append("DNI comprador: ").append(venta.getDniComprador() == null ? "N/D" : venta.getDniComprador()).append('\n');
		builder.append("Producto: ").append(venta.getNombreProductoManual()).append('\n');
		builder.append("Cantidad: ").append(String.format(Locale.getDefault(), "%.2f", venta.getCantidad())).append('\n');
		builder.append("Costo por unidad: ").append(String.format(Locale.getDefault(), "S/. %.2f", venta.getCostoUnitario())).append('\n');
		builder.append("Precio cajera: ").append(String.format(Locale.getDefault(), "S/. %.2f", venta.getPrecioUnitario())).append('\n');
		builder.append("IGV solo: ").append(String.format(Locale.getDefault(), "S/. %.2f", venta.getIgvMonto())).append('\n');
		builder.append("IGV aplicado: ").append(String.format(Locale.getDefault(), "%.0f%%", venta.getIgvPorcentaje() * 100)).append('\n');
		builder.append("Total: ").append(String.format(Locale.getDefault(), "S/. %.2f", venta.getTotal()));
		return builder.toString();
	}
}
