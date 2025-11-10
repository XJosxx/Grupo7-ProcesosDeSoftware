package com.example.apptienda.data;

import com.example.apptienda.modelos.modeloVenta;

import java.util.List;

/**
 * Repositorio de acceso a datos para ventas.
 */
public class VentaRepository implements com.example.apptienda.core.repositories.SaleRepository {

	private final DatabaseHelper dbHelper;

	public VentaRepository(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	@Override
	public modeloVenta registrar(modeloVenta venta) {
		return dbHelper.registrarVenta(venta);
	}

	@Override
	public List<modeloVenta> listar() {
		return dbHelper.listarVentas();
	}

	@Override
	public modeloVenta obtenerPorCodigo(String codigo) {
		return dbHelper.obtenerVentaPorCodigo(codigo);
	}

	@Override
	public modeloVenta obtenerPorId(long id) {
		return dbHelper.obtenerVentaPorId(id);
	}

	@Override
	public boolean actualizar(modeloVenta venta) {
		return dbHelper.actualizarVenta(venta);
	}

	@Override
	public boolean eliminar(long id) {
		return dbHelper.eliminarVenta(id);
	}

	@Override
	public String generarCodigo() {
		return dbHelper.generarSiguienteCodigoVenta();
	}
	
}
