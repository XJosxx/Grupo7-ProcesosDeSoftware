package com.example.myapplication.repositories;

import android.content.Context;
import com.example.myapplication.data.DatabaseHelper;
import com.example.myapplication.modelos.Usuario;

public class UsuarioRepository {
    private final DatabaseHelper db;

    public UsuarioRepository(Context ctx) {
        this.db = DatabaseHelper.getInstance(ctx);
    }

    public long crear(Usuario u) {
        return db.insertarUsuario(u);
    }

    public Usuario buscarPorEmail(String email) {
        return db.obtenerUsuarioPorEmail(email);
    }
}
