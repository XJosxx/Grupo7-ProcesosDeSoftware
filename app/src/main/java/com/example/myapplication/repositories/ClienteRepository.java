package com.example.myapplication.repositories;

import com.example.myapplication.entidades.Cliente;
import java.util.List;

public interface ClienteRepository {
    boolean save(Cliente cliente);
    boolean update(Cliente cliente);
    boolean delete(String dni);
    Cliente findByDni(String dni);
    List<Cliente> findAll();
}