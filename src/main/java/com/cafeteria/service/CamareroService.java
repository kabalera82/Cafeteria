package com.cafeteria.service;

import com.cafeteria.model.Camarero;
import com.cafeteria.repository.CamareroRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CamareroService {

    private final CamareroRepository repo;

    public CamareroService(CamareroRepository repo) {
        this.repo = repo;
    }

    public Camarero crear(Camarero camarero) {
        return repo.save(camarero);
    }

    public Optional<Camarero> buscarPorId(int id) {
        return repo.findById(id);
    }

    public Optional<Camarero> buscarPorNif(String nif) {
        return repo.findByNif(nif);
    }

    public List<Camarero> listarActivos() {
        return repo.findByActivoTrue();
    }

    public List<Camarero> listarTodos() {
        return repo.findAll();
    }

    public Camarero actualizar(Camarero camarero) {
        return repo.save(camarero);
    }

    public void darDeBaja(int id) {
        repo.findById(id).ifPresent(c -> {
            c.setActivo(false);
            repo.save(c);
        });
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }
}
