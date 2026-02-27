package org.example.helpdesk.controller;

import java.util.List;
import org.example.helpdesk.dto.CreateTicketCommentRequest;
import org.example.helpdesk.dto.CreateTicketRequest;
import org.example.helpdesk.dto.TicketResponse;
import org.example.helpdesk.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping
    public List<TicketResponse> findAll(@RequestParam(required = false) Long userId) {
        return ticketService.findAll(userId);
    }

    @GetMapping("/{id}")
    public TicketResponse findById(@PathVariable Long id) {
        return ticketService.findById(id);
    }

    @PutMapping("/{id}")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody CreateTicketRequest request) {
        return ticketService.update(id, request);
    }

    @PostMapping("/{id}/comments")
    public TicketResponse addComment(@PathVariable Long id, @Valid @RequestBody CreateTicketCommentRequest request) {
        return ticketService.addComment(id, request);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public TicketResponse deleteComment(@PathVariable Long id, @PathVariable Long commentId) {
        return ticketService.deleteComment(id, commentId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ticketService.delete(id);
    }
}
