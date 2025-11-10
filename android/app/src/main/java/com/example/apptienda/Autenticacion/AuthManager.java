package com.example.apptienda.Autenticacion;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * AuthManager
 * Clase simple para gestionar la sesión del usuario usando SharedPreferences.
 * Responsabilidades:
 * - Guardar y recuperar el token JWT (o similar) y metadatos mínimos (email, rol).
 * - Proveer un método `isLoggedIn()` que indica si hay sesión activa (token != null).
 * - Permitir cerrar sesión (logout) borrando las claves guardadas.
 *
 * Uso en el proyecto:
 * - `MainActivity` verifica `isLoggedIn()` para forzar la pantalla de autenticación.
 * - Otras partes de la app pueden llamar `getToken()` para adjuntar encabezados HTTP.
 *
 * Nota de seguridad:
 * - SharedPreferences no está cifrado por defecto; para datos sensibles (tokens críticos) considerar
 *   usar EncryptedSharedPreferences o un almacenamiento seguro.
 */
public class AuthManager implements com.example.apptienda.core.repositories.AuthRepository {
	private static final String PREFS = "auth_prefs";
	private static final String KEY_TOKEN = "jwt_token";
	private static final String KEY_EMAIL = "user_email";
	private static final String KEY_ROLE = "user_role";

	private final SharedPreferences prefs;

	public AuthManager(Context context) {
		// Se crea/abre el archivo de preferencias privado para la app
		prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
	}

	/**
	 * Guarda token y metadatos básicos (email, rol).
	 * - Se usa apply() para persistencia asíncrona; si se necesita bloqueo usar commit().
	 */
	@Override
    @TargetApi(11)
	public void saveSession(String token, String email, String role) {
		prefs.edit()
				.putString(KEY_TOKEN, token)
				.putString(KEY_EMAIL, email)
				.putString(KEY_ROLE, role)
				.apply();
	}

	// Guarda solamente el token (mantiene email/rol existentes)
	@Override
	public void saveToken(String token) {
		saveSession(token, getEmail(), getRole());
	}

	// Recupera el token almacenado (o null si no existe)
	@Override
	public String getToken() {
		return prefs.getString(KEY_TOKEN, null);
	}

	@Override
	public String getEmail() {
		return prefs.getString(KEY_EMAIL, null);
	}

	@Override
	public String getRole() {
		return prefs.getString(KEY_ROLE, "USER");
	}

	// Indica si hay sesión activa (token presente)
	@Override
	public boolean isLoggedIn() {
		return getToken() != null;
	}

	// Elimina la sesión guardada
	@Override
    @TargetApi(11)
	public void logout() {
		prefs.edit()
				.remove(KEY_TOKEN)
				.remove(KEY_EMAIL)
				.remove(KEY_ROLE)
				.apply();
	}
}
