package com.example.myapplication.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.modelos.ModeloCompra;
import com.example.myapplication.modelos.ModeloVenta;
import com.example.myapplication.modelos.Producto;
import com.example.myapplication.modelos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "apptienda.db";
    public static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_PRODUCTOS = "productos";
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String TABLE_VENTAS = "ventas";
    public static final String TABLE_COMPRAS = "compras";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductos = "CREATE TABLE " + TABLE_PRODUCTOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "precio_compra REAL," +
                "precio_venta REAL," +
                "stock REAL," +
                "categoria TEXT," +
                "imagen_url TEXT," +
                "activo INTEGER DEFAULT 1," +
                "fecha_creacion INTEGER," +
                "ultima_modificacion INTEGER" +
                ");";

        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "rol TEXT," +
                "fecha_creacion INTEGER" +
                ");";

        String createVentas = "CREATE TABLE " + TABLE_VENTAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "codigo TEXT UNIQUE," +
                "producto_id INTEGER," +
                "producto_nombre TEXT," +
                "dni_cliente TEXT," +
                "precio_unitario REAL," +
                "costo_unitario REAL," +
                "cantidad REAL," +
                "igv_porcentaje REAL," +
                "igv_monto REAL," +
                "total_sin_igv REAL," +
                "total_con_igv REAL," +
                "fecha INTEGER" +
                ");";

        String createCompras = "CREATE TABLE " + TABLE_COMPRAS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "producto_id INTEGER," +
                "proveedor_ruc TEXT," +
                "proveedor_nombre TEXT," +
                "precio_unitario REAL," +
                "cantidad REAL," +
                "total REAL," +
                "fecha INTEGER" +
                ");";

        db.execSQL(createProductos);
        db.execSQL(createUsuarios);
        db.execSQL(createVentas);
        db.execSQL(createCompras);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, drop and recreate. In production implement migrations.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
        onCreate(db);
    }

    // Productos CRUD
    public long insertarProducto(Producto p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", p.getNombre());
        cv.put("precio_compra", p.getPrecioCompra());
        cv.put("precio_venta", p.getPrecioVenta());
        cv.put("stock", p.getStock());
        cv.put("categoria", p.getCategoria());
        cv.put("imagen_url", p.getImagenUrl());
        cv.put("activo", p.getActivo());
        cv.put("fecha_creacion", p.getFechaCreacion());
        cv.put("ultima_modificacion", p.getUltimaModificacion());
        return db.insert(TABLE_PRODUCTOS, null, cv);
    }

    public Producto obtenerProducto(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTOS, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Producto p = new Producto();
                p.setId(c.getLong(c.getColumnIndexOrThrow("id")));
                p.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
                p.setPrecioCompra(c.getDouble(c.getColumnIndexOrThrow("precio_compra")));
                p.setPrecioVenta(c.getDouble(c.getColumnIndexOrThrow("precio_venta")));
                p.setStock(c.getDouble(c.getColumnIndexOrThrow("stock")));
                p.setCategoria(c.getString(c.getColumnIndexOrThrow("categoria")));
                p.setImagenUrl(c.getString(c.getColumnIndexOrThrow("imagen_url")));
                p.setActivo(c.getInt(c.getColumnIndexOrThrow("activo")));
                p.setFechaCreacion(c.getLong(c.getColumnIndexOrThrow("fecha_creacion")));
                p.setUltimaModificacion(c.getLong(c.getColumnIndexOrThrow("ultima_modificacion")));
                return p;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public List<Producto> obtenerTodosProductos() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRODUCTOS, null, "activo=1", null, null, null, "nombre ASC");
        try {
            if (c != null && c.moveToFirst()) {
                do {
                    Producto p = new Producto();
                    p.setId(c.getLong(c.getColumnIndexOrThrow("id")));
                    p.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
                    p.setPrecioCompra(c.getDouble(c.getColumnIndexOrThrow("precio_compra")));
                    p.setPrecioVenta(c.getDouble(c.getColumnIndexOrThrow("precio_venta")));
                    p.setStock(c.getDouble(c.getColumnIndexOrThrow("stock")));
                    p.setCategoria(c.getString(c.getColumnIndexOrThrow("categoria")));
                    p.setImagenUrl(c.getString(c.getColumnIndexOrThrow("imagen_url")));
                    p.setActivo(c.getInt(c.getColumnIndexOrThrow("activo")));
                    p.setFechaCreacion(c.getLong(c.getColumnIndexOrThrow("fecha_creacion")));
                    p.setUltimaModificacion(c.getLong(c.getColumnIndexOrThrow("ultima_modificacion")));
                    lista.add(p);
                } while (c.moveToNext());
            }
        } finally {
            if (c != null) c.close();
        }
        return lista;
    }

    // Usuarios
    public long insertarUsuario(Usuario u) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombre", u.getNombre());
        cv.put("email", u.getEmail());
        cv.put("password", u.getPassword());
        cv.put("rol", u.getRol());
        cv.put("fecha_creacion", u.getFechaCreacion());
        return db.insert(TABLE_USUARIOS, null, cv);
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USUARIOS, null, "email=?", new String[]{email}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                Usuario u = new Usuario();
                u.setId(c.getLong(c.getColumnIndexOrThrow("id")));
                u.setNombre(c.getString(c.getColumnIndexOrThrow("nombre")));
                u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                u.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
                u.setRol(c.getString(c.getColumnIndexOrThrow("rol")));
                u.setFechaCreacion(c.getLong(c.getColumnIndexOrThrow("fecha_creacion")));
                return u;
            }
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    // Ventas (registrar simple y ajustar stock)
    public long registrarVenta(ModeloVenta venta) throws IllegalStateException {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Ajustar stock si producto_id presente
            if (venta.getProductoId() > 0) {
                boolean ok = aplicarAjusteStock(db, venta.getProductoId(), -venta.getCantidad());
                if (!ok) throw new IllegalStateException("Stock insuficiente o producto no existe");
            }

            ContentValues cv = new ContentValues();
            cv.put("codigo", venta.getCodigo());
            cv.put("producto_id", venta.getProductoId());
            cv.put("producto_nombre", venta.getProductoNombre());
            cv.put("dni_cliente", venta.getDniCliente());
            cv.put("precio_unitario", venta.getPrecioUnitario());
            cv.put("costo_unitario", venta.getCostoUnitario());
            cv.put("cantidad", venta.getCantidad());
            cv.put("igv_porcentaje", venta.getIgvPorcentaje());
            cv.put("igv_monto", venta.getIgvMonto());
            cv.put("total_sin_igv", venta.getTotalSinIgv());
            cv.put("total_con_igv", venta.getTotalConIgv());
            cv.put("fecha", venta.getFecha());

            long id = db.insert(TABLE_VENTAS, null, cv);
            db.setTransactionSuccessful();
            return id;
        } finally {
            db.endTransaction();
        }
    }

    public boolean aplicarAjusteStock(SQLiteDatabase db, long productoId, double delta) {
        // db puede ser transaccional ya abierto
        Cursor c = db.query(TABLE_PRODUCTOS, new String[]{"stock"}, "id=?", new String[]{String.valueOf(productoId)}, null, null, null);
        try {
            if (c != null && c.moveToFirst()) {
                double stock = c.getDouble(c.getColumnIndexOrThrow("stock"));
                double nuevo = Math.round((stock + delta) * 100.0) / 100.0;
                if (nuevo < 0) return false;
                ContentValues cv = new ContentValues();
                cv.put("stock", nuevo);
                cv.put("ultima_modificacion", System.currentTimeMillis());
                int updated = db.update(TABLE_PRODUCTOS, cv, "id=?", new String[]{String.valueOf(productoId)});
                return updated > 0;
            }
        } finally {
            if (c != null) c.close();
        }
        return false;
    }

    // Compras
    public long registrarCompra(ModeloCompra compra) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put("producto_id", compra.getProductoId());
            cv.put("proveedor_ruc", compra.getProveedorRuc());
            cv.put("proveedor_nombre", compra.getProveedorNombre());
            cv.put("precio_unitario", compra.getPrecioUnitario());
            cv.put("cantidad", compra.getCantidad());
            cv.put("total", compra.getTotal());
            cv.put("fecha", compra.getFecha());
            long id = db.insert(TABLE_COMPRAS, null, cv);

            // actualizar stock
            aplicarAjusteStock(db, compra.getProductoId(), compra.getCantidad());

            db.setTransactionSuccessful();
            return id;
        } finally {
            db.endTransaction();
        }
    }
}
