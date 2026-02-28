package org.example.helpdesk.service;

import java.util.List;
import org.example.helpdesk.dto.CreateTicketCommentRequest;
import org.example.helpdesk.dto.CreateTicketRequest;
import org.example.helpdesk.dto.TicketCommentResponse;
import org.example.helpdesk.dto.TicketResponse;
import org.example.helpdesk.entity.Impact;
import org.example.helpdesk.entity.Priority;
import org.example.helpdesk.entity.SlaRule;
import org.example.helpdesk.entity.Ticket;
import org.example.helpdesk.entity.TicketComment;
import org.example.helpdesk.entity.TicketStatus;
import org.example.helpdesk.entity.User;
import org.example.helpdesk.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserServiceImpl userService;
    private final PriorityServiceImpl priorityService;
    private final ImpactServiceImpl impactService;
    private final SlaRuleService slaRuleService;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            UserServiceImpl userService,
            PriorityServiceImpl priorityService,
            ImpactServiceImpl impactService,
            SlaRuleService slaRuleService
    ) {
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.priorityService = priorityService;
        this.impactService = impactService;
        this.slaRuleService = slaRuleService;
    }

    @Override
    public TicketResponse create(CreateTicketRequest request) {
        User user = userService.findEntityById(request.getUserId());
        Priority priority = request.getPriorityId() == null ? null : priorityService.findEntityById(request.getPriorityId());
        Impact impact = request.getImpactId() == null ? null : impactService.findEntityById(request.getImpactId());
        Ticket ticket = new Ticket();
        apply(ticket, request, user, priority, impact);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketResponse> findAll(Long userId) {
        List<Ticket> tickets = userId == null
                ? ticketRepository.findAll()
                : ticketRepository.findByUserId(userId);
        return tickets.stream().map(this::toResponse).toList();
    }

    @Override
    public TicketResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Override
    public TicketResponse update(Long id, CreateTicketRequest request) {
        Ticket ticket = findEntityById(id);
        User user = userService.findEntityById(request.getUserId());
        Priority priority = request.getPriorityId() == null ? null : priorityService.findEntityById(request.getPriorityId());
        Impact impact = request.getImpactId() == null ? null : impactService.findEntityById(request.getImpactId());
        apply(ticket, request, user, priority, impact);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse addComment(Long ticketId, CreateTicketCommentRequest request) {
        Ticket ticket = findEntityById(ticketId);
        TicketComment comment = new TicketComment();
        comment.setCommentText(request.getCommentText());
        comment.setCreatedBy(request.getCreatedBy());
        ticket.addComment(comment);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse deleteComment(Long ticketId, Long commentId) {
        Ticket ticket = findEntityById(ticketId);
        TicketComment comment = ticket.getComments().stream()
                .filter(item -> commentId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ticket comment not found: " + commentId
                ));
        ticket.removeComment(comment);
        return toResponse(ticketRepository.save(ticket));
    }

    @Override
    public void delete(Long id) {
        Ticket ticket = findEntityById(id);
        ticketRepository.delete(ticket);
    }

    private Ticket findEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + id));
    }

    private void apply(Ticket ticket, CreateTicketRequest request, User user, Priority priority, Impact impact) {
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setUser(user);
        ticket.setPriority(priority);
        ticket.setImpact(impact);
        SlaRule matchedSla = slaRuleService.findMatchingForPriority(priority == null ? null : priority.getId());
        ticket.setSlaRule(matchedSla);
        ticket.setStatus(request.getStatus() == null ? TicketStatus.OPEN : request.getStatus());
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setUserId(ticket.getUser().getId());
        response.setUserName(ticket.getUser().getName());
        response.setPriorityId(ticket.getPriority() == null ? null : ticket.getPriority().getId());
        response.setPriorityName(ticket.getPriority() == null ? null : ticket.getPriority().getName());
        response.setImpactId(ticket.getImpact() == null ? null : ticket.getImpact().getId());
        response.setImpactName(ticket.getImpact() == null ? null : ticket.getImpact().getName());
        response.setSlaRuleId(ticket.getSlaRule() == null ? null : ticket.getSlaRule().getId());
        response.setSlaRuleName(ticket.getSlaRule() == null ? null : ticket.getSlaRule().getName());
        response.setComments(ticket.getComments().stream().map(this::toCommentResponse).toList());
        return response;
    }

    private TicketCommentResponse toCommentResponse(TicketComment comment) {
        TicketCommentResponse response = new TicketCommentResponse();
        response.setId(comment.getId());
        response.setCommentText(comment.getCommentText());
        response.setCreatedBy(comment.getCreatedBy());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
