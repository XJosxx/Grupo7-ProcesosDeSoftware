package com.example.myapplication.servicios.implementacion;

import com.example.myapplication.entidades.Usuario;
import com.example.myapplication.repositories.UsuarioRepository;
import com.example.myapplication.repositories.implementacion.UsuarioRepositoryImplementacion;
import com.example.myapplication.servicios.ServicioUsuario;

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