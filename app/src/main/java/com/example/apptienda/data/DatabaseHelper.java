package com.example.apptienda.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.apptienda.modelos.Producto;
import com.example.apptienda.modelos.Usuario;
import com.example.apptienda.modelos.modeloCompra;
import com.example.apptienda.modelos.modeloVenta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper centralizado para la base de datos local.
 * Gestiona productos, usuarios, ventas, compras y la logica de stock seguro.
 */
/**
 * DatabaseHelper
 * Clase central para manejar la base de datos SQLite local de la aplicación.
 * - Crea las tablas principales: productos, usuarios, ventas, compras.
 * - Proporciona métodos CRUD (insertar/obtener/actualizar/eliminar) para entidades.
 * - Implementa transacciones y ajustes de stock atómicos para ventas/compras.
 *
 * Conexiones con otros archivos:
 * - Modelos: usa las clases de `com.example.apptienda.modelos` (Producto, Usuario, modeloVenta, modeloCompra).
 * - Es utilizada por las capas de UI/servicios para persistencia local.
 *
 * Nota de diseño:
 * - Las operaciones que modifican stock usan transacciones y el helper `aplicarAjusteStock`.
 * - No usa Room; si se migra a Room, esta clase puede reemplazarse por DAOs/Entities.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final double IGV_POR_DEFECTO = 0.18d;

	private static final String DATABASE_NAME = "apptienda.db";
	private static final int DATABASE_VERSION = 2;

	// Tablas
	private static final String TABLE_PRODUCTOS = "productos";
	private static final String TABLE_USUARIOS = "usuarios";
	private static final String TABLE_VENTAS = "ventas";
	private static final String TABLE_COMPRAS = "compras";

	// Columnas comunes
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_NOMBRE = "nombre";
	private static final String COLUMN_FECHA = "fecha";

	// Columnas productos
	private static final String COLUMN_PRECIO_COMPRA = "precio_compra";
	private static final String COLUMN_PRECIO_VENTA = "precio_venta";
	private static final String COLUMN_STOCK = "stock";
	private static final String COLUMN_CATEGORIA = "categoria";
	private static final String COLUMN_IMAGEN_URL = "imagen_url";
	private static final String COLUMN_ACTIVO = "activo";
	private static final String COLUMN_FECHA_CREACION = "fecha_creacion";
	private static final String COLUMN_ULTIMA_MODIFICACION = "ultima_modificacion";

	// Columnas usuarios
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_PASSWORD = "password";
	private static final String COLUMN_ROL = "rol";

	// Columnas ventas
	private static final String COLUMN_VENTA_CODIGO = "codigo";
	private static final String COLUMN_VENTA_PRODUCTO_ID = "producto_id";
	private static final String COLUMN_VENTA_PRODUCTO_NOMBRE = "producto_nombre";
	private static final String COLUMN_VENTA_DNI = "dni_cliente";
	private static final String COLUMN_VENTA_PRECIO_UNITARIO = "precio_unitario";
	private static final String COLUMN_VENTA_COSTO_UNITARIO = "costo_unitario";
	private static final String COLUMN_VENTA_CANTIDAD = "cantidad";
	private static final String COLUMN_VENTA_IGV = "igv_porcentaje";
	private static final String COLUMN_VENTA_IGV_MONTO = "igv_monto";
	private static final String COLUMN_VENTA_TOTAL_SIN_IGV = "total_sin_igv";
	private static final String COLUMN_VENTA_TOTAL = "total_con_igv";

	// Columnas compras
	private static final String COLUMN_COMPRA_RUC = "proveedor_ruc";
	private static final String COLUMN_COMPRA_PROVEEDOR = "proveedor_nombre";
	private static final String COLUMN_COMPRA_PRECIO = "precio_unitario";
	private static final String COLUMN_COMPRA_CANTIDAD = "cantidad";
	private static final String COLUMN_COMPRA_TOTAL = "total";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);
		db.setForeignKeyConstraintsEnabled(true);
	}

	@Override
	// Se ejecuta la primera vez que se crea la BD: aquí se crean las tablas.
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_PRODUCTOS + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_NOMBRE + " TEXT NOT NULL,"
				+ COLUMN_PRECIO_COMPRA + " REAL NOT NULL,"
				+ COLUMN_PRECIO_VENTA + " REAL NOT NULL,"
				+ COLUMN_STOCK + " REAL NOT NULL,"
				+ COLUMN_CATEGORIA + " TEXT NOT NULL,"
				+ COLUMN_IMAGEN_URL + " TEXT,"
				+ COLUMN_ACTIVO + " INTEGER DEFAULT 1,"
				+ COLUMN_FECHA_CREACION + " INTEGER DEFAULT (strftime('%s','now')*1000),"
				+ COLUMN_ULTIMA_MODIFICACION + " INTEGER DEFAULT (strftime('%s','now')*1000)"
				+ ")");

		db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_NOMBRE + " TEXT NOT NULL,"
				+ COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
				+ COLUMN_PASSWORD + " TEXT NOT NULL,"
				+ COLUMN_ROL + " TEXT NOT NULL,"
				+ COLUMN_FECHA_CREACION + " INTEGER DEFAULT (strftime('%s','now')*1000)"
				+ ")");

		db.execSQL("CREATE TABLE " + TABLE_VENTAS + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_VENTA_CODIGO + " TEXT UNIQUE NOT NULL,"
				+ COLUMN_VENTA_PRODUCTO_ID + " INTEGER,"
				+ COLUMN_VENTA_PRODUCTO_NOMBRE + " TEXT,"
				+ COLUMN_VENTA_DNI + " TEXT,"
				+ COLUMN_VENTA_PRECIO_UNITARIO + " REAL NOT NULL,"
				+ COLUMN_VENTA_COSTO_UNITARIO + " REAL,"
				+ COLUMN_VENTA_CANTIDAD + " REAL NOT NULL,"
				+ COLUMN_VENTA_IGV + " REAL NOT NULL DEFAULT " + IGV_POR_DEFECTO + ","
				+ COLUMN_VENTA_IGV_MONTO + " REAL NOT NULL,"
				+ COLUMN_VENTA_TOTAL_SIN_IGV + " REAL NOT NULL,"
				+ COLUMN_VENTA_TOTAL + " REAL NOT NULL,"
				+ COLUMN_FECHA + " INTEGER DEFAULT (strftime('%s','now')*1000),"
				+ "FOREIGN KEY(" + COLUMN_VENTA_PRODUCTO_ID + ") REFERENCES " + TABLE_PRODUCTOS + "(" + COLUMN_ID + ")"
				+ ")");

		db.execSQL("CREATE TABLE " + TABLE_COMPRAS + " ("
				+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ COLUMN_VENTA_PRODUCTO_ID + " INTEGER NOT NULL,"
				+ COLUMN_COMPRA_RUC + " TEXT,"
				+ COLUMN_COMPRA_PROVEEDOR + " TEXT,"
				+ COLUMN_COMPRA_PRECIO + " REAL NOT NULL,"
				+ COLUMN_COMPRA_CANTIDAD + " REAL NOT NULL,"
				+ COLUMN_COMPRA_TOTAL + " REAL NOT NULL,"
				+ COLUMN_FECHA + " INTEGER DEFAULT (strftime('%s','now')*1000),"
				+ "FOREIGN KEY(" + COLUMN_VENTA_PRODUCTO_ID + ") REFERENCES " + TABLE_PRODUCTOS + "(" + COLUMN_ID + ")"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENTAS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
		onCreate(db);
	}

	// region Productos
	public long insertarProducto(Producto producto) {
		// Inserta un producto en la tabla `productos`.
		// - Recibe un objeto Producto (modelo) y lo transforma a ContentValues.
		// - Retorna el id (rowid) insertado o -1 si falla.
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = buildProductoValues(producto);
		return db.insert(TABLE_PRODUCTOS, null, values);
	}

	public Producto obtenerProducto(long id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_PRODUCTOS, null, COLUMN_ID + "=?",
				new String[]{String.valueOf(id)}, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapearProducto(cursor);
			}
			return null;
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	// Obtiene un producto por su id.
	// - Retorna null si no existe.


	public List<Producto> obtenerTodosLosProductos() {
		List<Producto> productos = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_PRODUCTOS, null, COLUMN_ACTIVO + "=1",
				null, null, COLUMN_NOMBRE, null);
		try {
			while (cursor != null && cursor.moveToNext()) {
				productos.add(mapearProducto(cursor));
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return productos;
	}

	public List<Producto> obtenerProductosPorCategoria(String categoria) {
		List<Producto> productos = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_PRODUCTOS, null, COLUMN_CATEGORIA + "=? AND " + COLUMN_ACTIVO + "=1",
				new String[]{categoria}, null, null, COLUMN_NOMBRE);
		try {
			while (cursor != null && cursor.moveToNext()) {
				productos.add(mapearProducto(cursor));
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return productos;
	}

	public boolean actualizarProducto(Producto producto) {
		producto.setUltimaModificacion(new Date());
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = buildProductoValues(producto);
		values.put(COLUMN_ULTIMA_MODIFICACION, producto.getUltimaModificacion().getTime());
		return db.update(TABLE_PRODUCTOS, values, COLUMN_ID + "=?",
				new String[]{String.valueOf(producto.getId())}) > 0;
	}

	public boolean eliminarProducto(long id) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_ACTIVO, 0);
		values.put(COLUMN_ULTIMA_MODIFICACION, System.currentTimeMillis());
		return db.update(TABLE_PRODUCTOS, values, COLUMN_ID + "=?",
				new String[]{String.valueOf(id)}) > 0;
	}

	private ContentValues buildProductoValues(Producto producto) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOMBRE, producto.getNombre());
		values.put(COLUMN_PRECIO_COMPRA, producto.getPrecioCompra());
		values.put(COLUMN_PRECIO_VENTA, producto.getPrecioVenta());
		values.put(COLUMN_STOCK, producto.getStock());
		values.put(COLUMN_CATEGORIA, producto.getCategoria());
		values.put(COLUMN_IMAGEN_URL, producto.getImagenUrl());
		values.put(COLUMN_ACTIVO, producto.isActivo() ? 1 : 0);
		if (producto.getFechaCreacion() != null) {
			values.put(COLUMN_FECHA_CREACION, producto.getFechaCreacion().getTime());
		}
		if (producto.getUltimaModificacion() != null) {
			values.put(COLUMN_ULTIMA_MODIFICACION, producto.getUltimaModificacion().getTime());
		}
		return values;
	}

	private Producto mapearProducto(Cursor cursor) {
		Producto producto = new Producto(
				cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
				cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_COMPRA)),
				cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO_VENTA)),
				cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_STOCK)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))
		);
		producto.setImagenUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEN_URL)));
		producto.setActivo(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACTIVO)) == 1);
		long fechaCreacion = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CREACION));
		long ultimaMod = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ULTIMA_MODIFICACION));
		producto.setFechaCreacion(new Date(fechaCreacion));
		producto.setUltimaModificacion(new Date(ultimaMod));
		return producto;
	}
	// endregion Productos

	// region Usuarios
	public long insertarUsuario(Usuario usuario) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOMBRE, usuario.getNombre());
		values.put(COLUMN_EMAIL, usuario.getEmail());
		values.put(COLUMN_PASSWORD, usuario.getPassword());
		values.put(COLUMN_ROL, usuario.getRol());
		return db.insert(TABLE_USUARIOS, null, values);
	}

	public Usuario obtenerUsuarioPorEmail(String email) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_USUARIOS, null, COLUMN_EMAIL + "=?",
				new String[]{email}, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapearUsuario(cursor);
			}
			return null;
		} finally {
			if (cursor != null) cursor.close();
		}
	}

	public List<Usuario> listarUsuarios() {
		List<Usuario> usuarios = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_USUARIOS, null, null, null, null, null, COLUMN_NOMBRE);
		try {
			while (cursor != null && cursor.moveToNext()) {
				usuarios.add(mapearUsuario(cursor));
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return usuarios;
	}

	public boolean actualizarUsuario(Usuario usuario) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NOMBRE, usuario.getNombre());
		values.put(COLUMN_EMAIL, usuario.getEmail());
		values.put(COLUMN_PASSWORD, usuario.getPassword());
		values.put(COLUMN_ROL, usuario.getRol());
		return db.update(TABLE_USUARIOS, values, COLUMN_ID + "=?",
				new String[]{String.valueOf(usuario.getId())}) > 0;
	}

	public boolean eliminarUsuario(long id) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_USUARIOS, COLUMN_ID + "=?",
				new String[]{String.valueOf(id)}) > 0;
	}

	private Usuario mapearUsuario(Cursor cursor) {
		return new Usuario(
				cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
				cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROL))
		);
	}
	// endregion Usuarios

	// region Ventas
	public modeloVenta registrarVenta(modeloVenta venta) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			// Registrar una venta implica varios pasos atómicos:
			// 1) Si la venta está ligada a un producto registrado, intenta ajustar el stock (-cantidad).
			// 2) Si no existe nombre manual, lo obtiene del producto.
			// 3) Genera un código único si falta y pone la fecha si no está.
			// 4) Recalcula totales (precio * cantidad, IGV, etc.) y persiste la fila en `ventas`.
			// 5) Si todo va bien, marca la transacción como exitosa.
			// Lanzará IllegalStateException en caso de stock insuficiente para deshacer la transacción.
			if (venta.tieneProductoRegistrado()) {
				if (!aplicarAjusteStock(db, venta.getProductoId(), -venta.getCantidadParaStock())) {
					throw new IllegalStateException("Stock insuficiente");
				}
				if (TextUtils.isEmpty(venta.getNombreProductoManual())) {
					Producto producto = obtenerProducto(venta.getProductoId());
					if (producto != null) {
						venta.setNombreProductoManual(producto.getNombre());
					}
				}
			}

			if (TextUtils.isEmpty(venta.getCodigo())) {
				venta.setCodigo(generarCodigoVenta(db));
			}
			if (venta.getFecha() == null) {
				venta.setFecha(new Date());
			}
			venta.recalcularTotales();

			ContentValues values = new ContentValues();
			values.put(COLUMN_VENTA_CODIGO, venta.getCodigo());
			if (venta.tieneProductoRegistrado()) {
				values.put(COLUMN_VENTA_PRODUCTO_ID, venta.getProductoId());
			} else {
				values.putNull(COLUMN_VENTA_PRODUCTO_ID);
			}
			values.put(COLUMN_VENTA_PRODUCTO_NOMBRE, venta.getNombreProductoManual());
			values.put(COLUMN_VENTA_DNI, venta.getDniComprador());
			values.put(COLUMN_VENTA_PRECIO_UNITARIO, venta.getPrecioUnitario());
			values.put(COLUMN_VENTA_COSTO_UNITARIO, venta.getCostoUnitario());
			values.put(COLUMN_VENTA_CANTIDAD, venta.getCantidad());
			values.put(COLUMN_VENTA_IGV, venta.getIgvPorcentaje());
			values.put(COLUMN_VENTA_IGV_MONTO, venta.getIgvMonto());
			values.put(COLUMN_VENTA_TOTAL_SIN_IGV, venta.getTotalSinIgv());
			values.put(COLUMN_VENTA_TOTAL, venta.getTotalConIgv());
			values.put(COLUMN_FECHA, venta.getFecha().getTime());

			long id = db.insertOrThrow(TABLE_VENTAS, null, values);
			venta.setId(id);
			db.setTransactionSuccessful();
			return venta;
		} finally {
			db.endTransaction();
		}
	}

	public boolean actualizarVenta(modeloVenta venta) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_VENTA_DNI, venta.getDniComprador());
		values.put(COLUMN_VENTA_PRECIO_UNITARIO, venta.getPrecioUnitario());
		values.put(COLUMN_VENTA_CANTIDAD, venta.getCantidad());
		values.put(COLUMN_VENTA_IGV, venta.getIgvPorcentaje());
		values.put(COLUMN_VENTA_IGV_MONTO, venta.getIgvMonto());
		values.put(COLUMN_VENTA_TOTAL_SIN_IGV, venta.getTotalSinIgv());
		values.put(COLUMN_VENTA_TOTAL, venta.getTotalConIgv());
		values.put(COLUMN_VENTA_PRODUCTO_NOMBRE, venta.getNombreProductoManual());
		return db.update(TABLE_VENTAS, values, COLUMN_ID + "=?",
				new String[]{String.valueOf(venta.getId())}) > 0;
	}

	public boolean eliminarVenta(long ventaId) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			modeloVenta venta = obtenerVentaPorIdInternal(db, ventaId);
			if (venta == null) {
				return false;
			}
			if (venta.tieneProductoRegistrado()) {
				if (!aplicarAjusteStock(db, venta.getProductoId(), venta.getCantidadParaStock())) {
					return false;
				}
			}
			int deleted = db.delete(TABLE_VENTAS, COLUMN_ID + "=?",
					new String[]{String.valueOf(ventaId)});
			db.setTransactionSuccessful();
			return deleted > 0;
		} finally {
			db.endTransaction();
		}
	}

	public List<modeloVenta> listarVentas() {
		List<modeloVenta> ventas = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_VENTAS, null, null, null, null, null, COLUMN_FECHA + " DESC");
		try {
			while (cursor != null && cursor.moveToNext()) {
				ventas.add(mapearVenta(cursor));
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return ventas;
	}

	public modeloVenta obtenerVentaPorCodigo(String codigo) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_VENTAS, null, COLUMN_VENTA_CODIGO + "=?",
				new String[]{codigo}, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapearVenta(cursor);
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return null;
	}

	public modeloVenta obtenerVentaPorId(long ventaId) {
		return obtenerVentaPorIdInternal(getReadableDatabase(), ventaId);
	}

	private modeloVenta obtenerVentaPorIdInternal(SQLiteDatabase db, long ventaId) {
		Cursor cursor = db.query(TABLE_VENTAS, null, COLUMN_ID + "=?",
				new String[]{String.valueOf(ventaId)}, null, null, null);
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return mapearVenta(cursor);
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return null;
	}

	public String generarSiguienteCodigoVenta() {
		return generarCodigoVenta(getReadableDatabase());
	}

	private String generarCodigoVenta(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery("SELECT IFNULL(MAX(" + COLUMN_ID + "),0) + 1 FROM " + TABLE_VENTAS, null);
		long correlativo = 1;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				correlativo = cursor.getLong(0);
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return String.format(Locale.ROOT, "V-%06d", correlativo);
	}

	private modeloVenta mapearVenta(Cursor cursor) {
		modeloVenta venta = new modeloVenta();
		venta.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
		venta.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENTA_CODIGO)));
		int productoIndex = cursor.getColumnIndexOrThrow(COLUMN_VENTA_PRODUCTO_ID);
		if (!cursor.isNull(productoIndex)) {
			venta.setProductoId(cursor.getLong(productoIndex));
		}
		venta.setNombreProductoManual(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENTA_PRODUCTO_NOMBRE)));
		venta.setDniComprador(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VENTA_DNI)));
		venta.setPrecioUnitario(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_PRECIO_UNITARIO)));
		int costoIndex = cursor.getColumnIndexOrThrow(COLUMN_VENTA_COSTO_UNITARIO);
		if (!cursor.isNull(costoIndex)) {
			venta.setCostoUnitario(cursor.getDouble(costoIndex));
		}
		venta.setCantidad(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_CANTIDAD)));
		venta.setIgvPorcentaje(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_IGV)));
		venta.setIgvMonto(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_IGV_MONTO)));
		venta.setTotalSinIgv(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_TOTAL_SIN_IGV)));
		venta.setTotalConIgv(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VENTA_TOTAL)));
		long fechaMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA));
		venta.setFecha(new Date(fechaMillis));
		return venta;
	}
	// endregion Ventas

	// region Compras
	public modeloCompra registrarCompra(modeloCompra compra) {
		// Registrar compra:
		// - Valida que exista producto asociado (por id o por objeto Producto).
		// - Inserta la fila en `compras` y ajusta el stock (+cantidad comprada).
		// - Si el precio de compra cambia, actualiza el producto con el nuevo precio.
		if (compra.getProductoId() <= 0 && compra.getProducto() != null) {
			compra.setProductoId(compra.getProducto().getId());
		}
		if (compra.getProductoId() <= 0) {
			throw new IllegalArgumentException("Se requiere un producto para registrar la compra");
		}

		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			values.put(COLUMN_VENTA_PRODUCTO_ID, compra.getProductoId());
			values.put(COLUMN_COMPRA_RUC, compra.getRucProveedor());
			values.put(COLUMN_COMPRA_PROVEEDOR, compra.getNombreProveedor());
			values.put(COLUMN_COMPRA_PRECIO, compra.getPrecioUnitario());
			values.put(COLUMN_COMPRA_CANTIDAD, compra.getCantidadComprada());
			values.put(COLUMN_COMPRA_TOTAL, compra.getTotal());
			values.put(COLUMN_FECHA, compra.getFechaCompra().getTime());

			long id = db.insertOrThrow(TABLE_COMPRAS, null, values);
			compra.setId(id);

			if (!aplicarAjusteStock(db, compra.getProductoId(), compra.getCantidadComprada())) {
				throw new IllegalStateException("No se pudo actualizar el stock");
			}

			Producto producto = obtenerProducto(compra.getProductoId());
			if (producto != null && producto.getPrecioCompra() != compra.getPrecioUnitario()) {
				producto.setPrecioCompra(compra.getPrecioUnitario());
				actualizarProducto(producto);
			}

			db.setTransactionSuccessful();
			return compra;
		} finally {
			db.endTransaction();
		}
	}

	public List<modeloCompra> listarCompras() {
		List<modeloCompra> compras = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_COMPRAS, null, null, null, null, null, COLUMN_FECHA + " DESC");
		try {
			while (cursor != null && cursor.moveToNext()) {
				compras.add(mapearCompra(cursor));
			}
		} finally {
			if (cursor != null) cursor.close();
		}
		return compras;
	}

	public boolean eliminarCompra(long compraId) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_COMPRAS, COLUMN_ID + "=?",
				new String[]{String.valueOf(compraId)}) > 0;
	}

	private modeloCompra mapearCompra(Cursor cursor) {
		modeloCompra compra = new modeloCompra();
		compra.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
		compra.setProductoId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_VENTA_PRODUCTO_ID)));
		compra.setRucProveedor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPRA_RUC)));
		compra.setNombreProveedor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPRA_PROVEEDOR)));
		compra.setPrecioUnitario(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COMPRA_PRECIO)));
		compra.setCantidadComprada(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COMPRA_CANTIDAD)));
		compra.setTotal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COMPRA_TOTAL)));
		compra.setFechaCompra(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_FECHA))));
		return compra;
	}
	// endregion Compras

	// region Helpers
	private boolean aplicarAjusteStock(SQLiteDatabase db, long productoId, double delta) {
		// Ajuste de stock seguro:
		// - Lee el stock actual, calcula el nuevo stock aplicando `delta` (positivo para compras, negativo para ventas)
		// - Redondea a 2 decimales y evita stock negativo
		// - Actualiza la fila en la tabla productos y devuelve true si la operación se realizó correctamente
		// - Diseñado para ser invocado dentro de transacciones cuando sea necesario
		Cursor cursor = db.query(TABLE_PRODUCTOS, new String[]{COLUMN_STOCK}, COLUMN_ID + "=?",
				new String[]{String.valueOf(productoId)}, null, null, null);
		double stockActual = -1;
		try {
			if (cursor != null && cursor.moveToFirst()) {
				stockActual = cursor.getDouble(0);
			}
		} finally {
			if (cursor != null) cursor.close();
		}

		if (stockActual < 0) {
			return false;
		}

		double nuevoStock = Math.round((stockActual + delta) * 100.0d) / 100.0d;
		if (nuevoStock < 0) {
			return false;
		}

		ContentValues values = new ContentValues();
		values.put(COLUMN_STOCK, nuevoStock);
		values.put(COLUMN_ULTIMA_MODIFICACION, System.currentTimeMillis());
		return db.update(TABLE_PRODUCTOS, values, COLUMN_ID + "=?",
				new String[]{String.valueOf(productoId)}) > 0;
	}
	// endregion Helpers
}
