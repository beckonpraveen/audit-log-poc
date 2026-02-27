package org.example.helpdesk.repository;

import java.util.List;
import org.example.helpdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
}
