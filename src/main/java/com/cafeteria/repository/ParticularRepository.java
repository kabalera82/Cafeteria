package com.cafeteria.repository;

import com.cafeteria.model.Particular;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticularRepository extends JpaRepository<Particular, String> {
    List<Particular> findByActivoTrue();
}
