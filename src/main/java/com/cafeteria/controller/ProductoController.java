package com.cafeteria.controller;

import com.cafeteria.model.Producto;
import com.cafeteria.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listarActivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscar(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return service.crear(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable String id, @RequestBody Producto producto) {
        return service.buscarPorId(id)
                .map(existing -> ResponseEntity.ok(service.actualizar(producto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable String id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
