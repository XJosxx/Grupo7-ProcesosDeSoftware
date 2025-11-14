package com.example.myapplication.repositories;

import android.content.Context;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.modelos.ModeloCompra;

public class CompraRepository {
    private final DatabaseHelper db;

    public CompraRepository(Context ctx) {
        this.db = DatabaseHelper.getInstance(ctx);
    }

    public long registrar(ModeloCompra c) {
        return db.registrarCompra(c);
    }
}
