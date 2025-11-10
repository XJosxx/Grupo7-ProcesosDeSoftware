package com.example.apptienda.servicios;

import android.util.Base64;

import com.example.apptienda.Autenticacion.AuthManager;
import com.example.apptienda.core.repositories.AuthRepository;
import com.example.apptienda.data.UsuarioRepository;
import com.example.apptienda.modelos.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Servicio encargado de registrar usuarios, validar credenciales
 * y coordinar el almacenamiento de la sesión local.
 *
 * Diseño:
 * - Depende de `AuthRepository` (abstracción para persistencia de sesión). Esto permite
 *   sustituir el almacenamiento de sesión (SharedPreferences, SecureStore, etc.) sin cambiar
 *   la lógica de negocio.
 * - Mantiene la dependencia sobre `UsuarioRepository` para operaciones de usuario (crear/buscar).
 *
 * Nota de seguridad:
 * - El hashing actual usa SHA-256 + Base64 solo como ejemplo. Para producción usar sal y
 *   algoritmos dedicados como BCrypt o Argon2. Además considerar verificación de correo y
 *   protección contra ataques de fuerza bruta.
 */
public class ServicioAuth {

	private final UsuarioRepository usuarioRepository;
	private final AuthRepository authRepository;

	/**
	 * Constructor principal: inyecta una abstracción `AuthRepository`.
	 */
	public ServicioAuth(UsuarioRepository usuarioRepository, AuthRepository authRepository) {
		this.usuarioRepository = usuarioRepository;
		this.authRepository = authRepository;
	}

	/**
	 * Constructor de compatibilidad: acepta `AuthManager` (implementación actual basada en
	 * SharedPreferences) para no romper llamadas existentes.
	 */
	public ServicioAuth(UsuarioRepository usuarioRepository, AuthManager authManager) {
		this(usuarioRepository, (AuthRepository) authManager);
	}

	/** Crea un usuario nuevo tras validar que el email no exista. */
	public Usuario registrar(String nombre, String email, String password, String rol) throws Exception {
		if (usuarioRepository.buscarPorEmail(email) != null) {
			throw new Exception("Ya existe un usuario con este correo");
		}
		String passwordHash = hashPassword(password);
		Usuario usuario = new Usuario(0, nombre, email, passwordHash, rol);
		return usuarioRepository.crear(usuario);
	}

	/** Valida credenciales y devuelve el usuario si coinciden. */
	public Usuario login(String email, String password) throws Exception {
		Usuario usuario = usuarioRepository.buscarPorEmail(email);
		if (usuario == null) {
			throw new Exception("Usuario no encontrado");
		}
		String passwordHash = hashPassword(password);
		if (!passwordHash.equals(usuario.getPassword())) {
			throw new Exception("Credenciales invalidas");
		}
		return usuario;
	}

	/**
	 * Guarda la sesión local del usuario delegando en AuthRepository.
	 * - Genera un token simple (ejemplo) y lo persiste junto al email/rol.
	 */
	public void guardarSesion(Usuario usuario) {
		String token = generarToken(usuario);
		authRepository.saveSession(token, usuario.getEmail(), usuario.getRol());
	}

	/** Cierra la sesión delegando en AuthRepository. */
	public void logout() {
		authRepository.logout();
	}

	private String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		return Base64.encodeToString(hash, Base64.NO_WRAP);
	}

	private String generarToken(Usuario usuario) {
		String payload = usuario.getEmail() + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
		return Base64.encodeToString(payload.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
	}
}
