package com.example.apptienda.data;

import com.example.apptienda.modelos.modeloCompra;

import java.util.List;

/**
 * CompraRepository
 * - Pequeña capa de abstracción sobre `DatabaseHelper` para operaciones relacionadas con compras
 *   (reabastecimientos).
 * - Proporciona métodos simples usados por la UI o servicios para registrar, listar y eliminar compras.
 *
 * Por qué existe:
 * - Mantiene desacoplada la lógica de persistencia (DatabaseHelper) de los controladores/actividades.
 * - Facilita pruebas/mocks en caso de necesitar reemplazar la fuente de datos.
 */
public class CompraRepository implements com.example.apptienda.core.repositories.PurchaseRepository {

	private final DatabaseHelper dbHelper;

	public CompraRepository(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	/**
	 * Registra una compra delegando a DatabaseHelper.
	 * - Lanza excepciones si los parámetros no son válidos (según DatabaseHelper).
	 */
	@Override
	public modeloCompra registrar(modeloCompra compra) {
		return dbHelper.registrarCompra(compra);
	}

	/**
	 * Lista todas las compras ordenadas por fecha descendente.
	 */
	@Override
	public List<modeloCompra> listar() {
		return dbHelper.listarCompras();
	}

	/**
	 * Elimina una compra por id.
	 */
	@Override
	public boolean eliminar(long compraId) {
		return dbHelper.eliminarCompra(compraId);
	}
}
