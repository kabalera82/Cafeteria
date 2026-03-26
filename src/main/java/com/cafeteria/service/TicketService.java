package com.cafeteria.service;

import com.cafeteria.model.CabeceraTicket;
import com.cafeteria.model.LineaTicket;
import com.cafeteria.model.Producto;
import com.cafeteria.repository.CabeceraTicketRepository;
import com.cafeteria.repository.LineaTicketRepository;
import com.cafeteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final CabeceraTicketRepository ticketRepo;
    private final LineaTicketRepository lineaRepo;
    private final ProductoRepository productoRepo;

    public TicketService(CabeceraTicketRepository ticketRepo,
                         LineaTicketRepository lineaRepo,
                         ProductoRepository productoRepo) {
        this.ticketRepo = ticketRepo;
        this.lineaRepo = lineaRepo;
        this.productoRepo = productoRepo;
    }

    @Transactional
    public CabeceraTicket crearTicket(CabeceraTicket ticket) {
        return ticketRepo.save(ticket);
    }

    @Transactional
    public CabeceraTicket agregarLinea(String numTicket, String idProducto, int cantidad) {
        CabeceraTicket ticket = ticketRepo.findById(numTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado: " + numTicket));

        Producto producto = productoRepo.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

        LineaTicket linea = new LineaTicket(producto, cantidad);
        ticket.agregarLinea(linea);

        return ticketRepo.save(ticket);
    }

    public Optional<CabeceraTicket> buscarPorNumero(String numTicket) {
        return ticketRepo.findById(numTicket);
    }

    public List<CabeceraTicket> listarTodos() {
        return ticketRepo.findAll();
    }

    public List<CabeceraTicket> listarPorCliente(String idCliente) {
        return ticketRepo.findByClienteIdCliente(idCliente);
    }

    public List<CabeceraTicket> listarPorCamarero(int idCamarero) {
        return ticketRepo.findByCamareroIdCamarero(idCamarero);
    }

    @Transactional
    public void eliminarTicket(String numTicket) {
        ticketRepo.deleteById(numTicket);
    }
}
