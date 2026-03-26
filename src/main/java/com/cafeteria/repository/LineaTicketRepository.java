package com.cafeteria.repository;

import com.cafeteria.model.LineaTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LineaTicketRepository extends JpaRepository<LineaTicket, Integer> {
    List<LineaTicket> findByCabeceraTicketNumTicket(String numTicket);
}
