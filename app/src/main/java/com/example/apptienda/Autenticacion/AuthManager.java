package com.example.apptienda.Autenticacion;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestor simple de autenticación que guarda token en SharedPreferences.
 * Este es un ejemplo básico: para producción evita almacenar tokens inseguros y usa storage cifrado.
 */
public class AuthManager {
	private static final String PREFS = "auth_prefs";
	private static final String KEY_TOKEN = "jwt_token";
	private SharedPreferences prefs;

	public AuthManager(Context context) {
		prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
	}

	public void saveToken(String token) {
		// Guardado simple del token. Para mayor seguridad usa EncryptedSharedPreferences o Keystore.
		prefs.edit().putString(KEY_TOKEN, token).apply();
	}

	public String getToken() {
		return prefs.getString(KEY_TOKEN, null);
	}

	public boolean isLoggedIn() {
		// Si hay token almacenado asumimos sesión iniciada (puedes validar expiración aquí).
		return getToken() != null;
	}

	public void logout() {
		prefs.edit().remove(KEY_TOKEN).apply();
	}
}
