package com.example.apptienda.Autenticacion;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

/**
 * Utilidad para manejar tokens JWT.
 * NOTA: Esta es una implementacion basica para desarrollo. Para produccion:
 * 1. Usa una biblioteca JWT completa
 * 2. Verifica firmas
 * 3. Valida tiempo de expiracion
 */
public class JwtUtils {
    
    // Extrae el campo "email" del payload de un JWT (sin verificar firma).
    // - Decodifica la segunda parte (payload) en Base64 y parsea JSON.
    // - Retorna null si el token no tiene formato esperado o si ocurre un error.
    // ADVERTENCIA: No valida la firma ni la expiración; usar solo para funcionalidades UI/depuración.
    public static String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT), StandardCharsets.UTF_8);
            JSONObject jsonPayload = new JSONObject(payload);
            
            return jsonPayload.optString("email", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTokenExpired(String token) {
        // Verifica si el JWT contiene un claim `exp` y si ya expiró.
        // - Si el token no está bien formado o no tiene `exp`, retorna true (considerado expirado).
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return true;
            }

            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT), StandardCharsets.UTF_8);
            JSONObject jsonPayload = new JSONObject(payload);
            
            long exp = jsonPayload.optLong("exp", 0);
            return exp < (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static String getRoleFromToken(String token) {
        // Extrae el campo `role` del payload. Retorna "USER" por defecto si no está presente.
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT), StandardCharsets.UTF_8);
            JSONObject jsonPayload = new JSONObject(payload);
            
            return jsonPayload.optString("role", "USER");
        } catch (Exception e) {
            e.printStackTrace();
            return "USER";
        }
    }
}
