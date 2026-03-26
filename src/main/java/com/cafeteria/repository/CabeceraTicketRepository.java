package com.cafeteria.repository;

import com.cafeteria.model.CabeceraTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CabeceraTicketRepository extends JpaRepository<CabeceraTicket, String> {
    List<CabeceraTicket> findByClienteIdCliente(String idCliente);
    List<CabeceraTicket> findByCamareroIdCamarero(int idCamarero);
    List<CabeceraTicket> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
}
