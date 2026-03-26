package com.cafeteria.controller;

import com.cafeteria.model.Cliente;
import com.cafeteria.model.Empresa;
import com.cafeteria.model.Particular;
import com.cafeteria.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listarActivos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable String id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/empresas")
    public List<Empresa> listarEmpresas() {
        return service.listarEmpresas();
    }

    @GetMapping("/particulares")
    public List<Particular> listarParticulares() {
        return service.listarParticulares();
    }

    @PostMapping("/empresa")
    public Empresa crearEmpresa(@RequestBody Empresa empresa) {
        return service.crearEmpresa(empresa);
    }

    @PostMapping("/particular")
    public Particular crearParticular(@RequestBody Particular particular) {
        return service.crearParticular(particular);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable String id, @RequestBody Cliente cliente) {
        return service.buscarPorId(id)
                .map(existing -> ResponseEntity.ok(service.actualizar(cliente)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable String id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
