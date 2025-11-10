package com.example.apptienda.servicios;

import com.example.apptienda.data.CompraRepository;
import com.example.apptienda.data.DatabaseHelper;
import com.example.apptienda.core.repositories.PurchaseRepository;
import com.example.apptienda.modelos.Producto;
import com.example.apptienda.modelos.modeloCompra;

import java.util.Date;
import java.util.List;

/**
 * Servicio para manejar la lógica de negocio de compras (reabastecimientos).
 *
 * Responsabilidad:
 * - Orquesta la creación de registros de compra, valida integridad de datos y delega la
 *   persistencia a `PurchaseRepository`.
 * - Diseñado para depender de la interfaz `PurchaseRepository` con un constructor de
 *   compatibilidad que acepta `DatabaseHelper` y construye el `CompraRepository` existente.
 *
 * Conexiones principales:
 * - `ServicioProducto`: se usa para obtener el producto y sus datos actuales (codigo, stock).
 * - `PurchaseRepository` (`CompraRepository` en `data/`): operaciones CRUD sobre compras.
 */
public class ServicioCompra {
	private final PurchaseRepository compraRepository;
	private final ServicioProducto servicioProducto;

	/** Constructor principal - inyecta la abstracción PurchaseRepository */
	public ServicioCompra(PurchaseRepository compraRepository, ServicioProducto servicioProducto) {
		this.compraRepository = compraRepository;
		this.servicioProducto = servicioProducto;
	}

	/** Constructor de compatibilidad: acepta DatabaseHelper y construye CompraRepository */
	public ServicioCompra(DatabaseHelper dbHelper, ServicioProducto servicioProducto) {
		this(new CompraRepository(dbHelper), servicioProducto);
	}

	public modeloCompra registrarCompra(long productoId, String ruc, double precioCompra,
	                                    double cantidad, String proveedor) throws Exception {
		Producto producto = servicioProducto.obtenerProductoPorId(productoId);
		if (producto == null) {
			throw new Exception("Producto no encontrado");
		}
		if (cantidad <= 0) {
			throw new Exception("La cantidad debe ser mayor a cero");
		}

		modeloCompra compra = new modeloCompra();
		compra.setProducto(producto);
		compra.setProductoId(productoId);
		compra.setRucProveedor(ruc);
		compra.setNombreProveedor(proveedor);
		compra.setPrecioUnitario(precioCompra);
		compra.setCantidadComprada(cantidad);
		compra.setFechaCompra(new Date());

		return compraRepository.registrar(compra);
	}

	/** Lista las compras registradas (delegado al repositorio). */
	public List<modeloCompra> listarCompras() {
		return compraRepository.listar();
	}

	/** Elimina una compra por id (delegado al repositorio). */
	public boolean eliminarCompra(long compraId) {
		return compraRepository.eliminar(compraId);
	}
}
