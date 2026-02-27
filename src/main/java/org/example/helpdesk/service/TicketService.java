package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateTicketCommentRequest;
import org.example.helpdesk.dto.CreateTicketRequest;
import org.example.helpdesk.dto.TicketResponse;

public interface TicketService {
    TicketResponse create(CreateTicketRequest request);

    List<TicketResponse> findAll(Long userId);

    TicketResponse findById(Long id);

    TicketResponse update(Long id, CreateTicketRequest request);

    TicketResponse addComment(Long ticketId, CreateTicketCommentRequest request);

    TicketResponse deleteComment(Long ticketId, Long commentId);

    void delete(Long id);
}
