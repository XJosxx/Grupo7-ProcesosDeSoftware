package com.example.apptienda.core.repositories;

/**
 * Interfaz que abstrae operaciones de autenticación/gestión de sesión.
 */
public interface AuthRepository {
    void saveSession(String token, String email, String role);
    void saveToken(String token);
    String getToken();
    String getEmail();
    String getRole();
    boolean isLoggedIn();
    void logout();
}
