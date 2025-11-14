package com.example.myapplication.servicios;

import android.content.Context;
import android.util.Base64;

import com.example.myapplication.autenticacion.AuthManager;
import com.example.myapplication.modelos.Usuario;
import com.example.myapplication.repositories.UsuarioRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class ServicioAutenticacion {
    private final UsuarioRepository repo;
    private final AuthManager authManager;

    public ServicioAutenticacion(Context ctx) {
        this.repo = new UsuarioRepository(ctx);
        this.authManager = new AuthManager(ctx);
    }

    public Usuario registrar(String nombre, String email, String password, String rol) throws Exception {
        Usuario existing = repo.buscarPorEmail(email);
        if (existing != null) throw new Exception("Email ya registrado");

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setRol(rol);
        u.setFechaCreacion(System.currentTimeMillis());

        String hashed = hashPassword(password);
        u.setPassword(hashed);

        long id = repo.crear(u);
        u.setId(id);
        return u;
    }

    public Usuario login(String email, String password) throws Exception {
        Usuario u = repo.buscarPorEmail(email);
        if (u == null) throw new Exception("Credenciales inválidas");
        String hashed = hashPassword(password);
        if (!hashed.equals(u.getPassword())) throw new Exception("Credenciales inválidas");

        String token = generarToken(email);
        authManager.saveSession(email, token, u.getRol());
        return u;
    }

    private String generarToken(String email) {
        String raw = email + ":" + System.currentTimeMillis() + ":" + new SecureRandom().nextInt(999999);
        return Base64.encodeToString(raw.getBytes(), Base64.NO_WRAP);
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(password.getBytes("UTF-8"));
        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }
}
