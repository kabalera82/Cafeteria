package com.cafeteria.controller;

import com.cafeteria.model.Camarero;
import com.cafeteria.service.CamareroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/camareros")
public class CamareroController {

    private final CamareroService service;

    public CamareroController(CamareroService service) {
        this.service = service;
    }

    @GetMapping
    public List<Camarero> listar() {
        return service.listarActivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Camarero> buscar(@PathVariable int id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Camarero crear(@RequestBody Camarero camarero) {
        return service.crear(camarero);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Camarero> actualizar(@PathVariable int id, @RequestBody Camarero camarero) {
        return service.buscarPorId(id)
                .map(existing -> {
                    camarero.setIdCamarero(id);
                    return ResponseEntity.ok(service.actualizar(camarero));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable int id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
