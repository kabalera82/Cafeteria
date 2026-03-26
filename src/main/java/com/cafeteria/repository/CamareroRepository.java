package com.cafeteria.repository;

import com.cafeteria.model.Camarero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CamareroRepository extends JpaRepository<Camarero, Integer> {
    List<Camarero> findByActivoTrue();
    Optional<Camarero> findByNif(String nif);
}
