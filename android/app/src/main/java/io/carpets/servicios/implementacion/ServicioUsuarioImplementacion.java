package io.carpets.servicios.implementacion;

import io.carpets.entidades.Usuario;
import io.carpets.repositories.UsuarioRepository;
import io.carpets.repositories.implementacion.UsuarioRepositoryImplementacion;
import io.carpets.servicios.ServicioUsuario;

/*
    Implementación del servicio para la entidad Usuario
    se encarga de la lógica de negocio relacionada con los usuarios.
*/
public class ServicioUsuarioImplementacion implements ServicioUsuario {

    private UsuarioRepository repo = new UsuarioRepositoryImplementacion();

    @Override
    public Usuario login(String nombre, String password) {

        Usuario u = repo.findByUsername(nombre);
        if (u != null && u.getPassword().equals(password)) {
            return u;
        }
        return null;
    }
}