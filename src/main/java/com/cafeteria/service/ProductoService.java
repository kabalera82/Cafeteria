package com.cafeteria.service;

import com.cafeteria.model.Producto;
import com.cafeteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public Producto crear(Producto producto) {
        return repo.save(producto);
    }

    public Optional<Producto> buscarPorId(String id) {
        return repo.findById(id);
    }

    public List<Producto> listarActivos() {
        return repo.findByActivoTrue();
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Producto actualizar(Producto producto) {
        return repo.save(producto);
    }

    public void darDeBaja(String id) {
        repo.findById(id).ifPresent(p -> {
            p.setActivo(false);
            repo.save(p);
        });
    }

    public void actualizarStock(String id, int cantidad) {
        repo.findById(id).ifPresent(p -> {
            p.setStock(p.getStock() + cantidad);
            repo.save(p);
        });
    }
}
