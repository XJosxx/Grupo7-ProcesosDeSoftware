package com.example.apptienda.data;

import com.example.apptienda.modelos.Usuario;

import java.util.List;

/**
 * UsuarioRepository
 * - Capa de acceso a datos para la entidad Usuario.
 * - Provee métodos CRUD simples (crear, buscar por email, listar, actualizar, eliminar).
 * - Implementación ligera que delega en `DatabaseHelper`.
 *
 * Conexiones:
 * - `DatabaseHelper` (persistencia SQLite) es la implementación concreta.
 * - `ServicioAuth` y otras clases de la capa de negocio usan este repositorio para
 *   operaciones relacionadas con usuarios.
 *
 * Notas:
 * - Para tests, reemplazar esta clase por un mock o una implementación en memoria.
 * - Si migras a Room, implementar un DAO con la misma semántica.
 */
public class UsuarioRepository {

	private final DatabaseHelper dbHelper;

	public UsuarioRepository(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public Usuario crear(Usuario usuario) {
		long id = dbHelper.insertarUsuario(usuario);
		usuario.setId((int) id);
		return usuario;
	}

	public Usuario buscarPorEmail(String email) {
		return dbHelper.obtenerUsuarioPorEmail(email);
	}

	public List<Usuario> listar() {
		return dbHelper.listarUsuarios();
	}

	public boolean actualizar(Usuario usuario) {
		return dbHelper.actualizarUsuario(usuario);
	}

	public boolean eliminar(int usuarioId) {
		return dbHelper.eliminarUsuario(usuarioId);
	}
}
