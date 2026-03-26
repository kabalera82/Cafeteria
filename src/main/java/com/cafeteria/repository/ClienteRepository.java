package com.cafeteria.repository;

import com.cafeteria.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
    List<Cliente> findByActivoTrue();
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}
