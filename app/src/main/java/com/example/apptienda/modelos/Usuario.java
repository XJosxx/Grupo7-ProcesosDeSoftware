package com.example.apptienda.modelos;

/**
 * Usuario
 * - Modelo que representa un usuario del sistema.
 * - Campos importantes: id, nombre, email, password (almacenado con hashing), rol.
 * - Uso: empleado por `ServicioAuth` para registrar/login y por `AuthManager` (o
 *   implementaciones de `AuthRepository`) para persistir metadatos de sesión.
 *
 * Notas de seguridad:
 * - El campo `password` en este modelo representa el hash de la contraseña.
 * - No exponer el campo `password` en logs o transferencias inseguras.
 */
public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private String rol;

    public Usuario(int id, String nombre, String email, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
}