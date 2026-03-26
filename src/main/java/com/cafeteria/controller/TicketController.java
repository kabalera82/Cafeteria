package com.cafeteria.controller;

import com.cafeteria.model.CabeceraTicket;
import com.cafeteria.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public List<CabeceraTicket> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{numTicket}")
    public ResponseEntity<CabeceraTicket> buscar(@PathVariable String numTicket) {
        return service.buscarPorNumero(numTicket)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public List<CabeceraTicket> porCliente(@PathVariable String idCliente) {
        return service.listarPorCliente(idCliente);
    }

    @GetMapping("/camarero/{idCamarero}")
    public List<CabeceraTicket> porCamarero(@PathVariable int idCamarero) {
        return service.listarPorCamarero(idCamarero);
    }

    @PostMapping
    public CabeceraTicket crear(@RequestBody CabeceraTicket ticket) {
        return service.crearTicket(ticket);
    }

    @PostMapping("/{numTicket}/lineas")
    public ResponseEntity<CabeceraTicket> agregarLinea(
            @PathVariable String numTicket,
            @RequestBody Map<String, Object> body) {
        String idProducto = (String) body.get("idProducto");
        int cantidad = (int) body.get("cantidad");
        return ResponseEntity.ok(service.agregarLinea(numTicket, idProducto, cantidad));
    }

    @DeleteMapping("/{numTicket}")
    public ResponseEntity<Void> eliminar(@PathVariable String numTicket) {
        service.eliminarTicket(numTicket);
        return ResponseEntity.noContent().build();
    }
}
