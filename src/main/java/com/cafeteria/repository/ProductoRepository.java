package com.cafeteria.repository;

import com.cafeteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findByActivoTrue();
    List<Producto> findByDescripcionContainingIgnoreCase(String descripcion);
}
