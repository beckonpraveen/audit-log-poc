package org.example.helpdesk.audit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import org.example.helpdesk.audit.dto.AuditLogResponse;
import org.example.helpdesk.audit.entity.AuditLogRecord;
import org.example.helpdesk.audit.repository.AuditLogRepository;
import org.example.helpdesk.repository.TicketRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AuditLogServiceImpl implements AuditLogService {

    private static final String TICKET_ENTITY = "Ticket";
    private final AuditLogRepository auditLogRepository;
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository,
            TicketRepository ticketRepository,
            ObjectMapper objectMapper
    ) {
        this.auditLogRepository = auditLogRepository;
        this.ticketRepository = ticketRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<AuditLogResponse> findByTicketId(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found: " + ticketId);
        }

        String ticketIdText = String.valueOf(ticketId);
        List<AuditLogRecord> result = auditLogRepository.findForEntityOrParent(TICKET_ENTITY, ticketIdText);

        return result.stream()
                .sorted(Comparator.comparing(AuditLogRecord::getChangedAt).thenComparing(AuditLogRecord::getId))
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLogRecord record) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(record.getId());
        response.setEntityName(record.getEntityName());
        response.setEntityId(record.getEntityId());
        response.setParentEntityName(record.getParentEntityName());
        response.setParentEntityId(record.getParentEntityId());
        response.setOperation(record.getOperation());
        response.setChangedAt(record.getChangedAt());
        response.setActor(record.getActor());
        response.setData(parseJson(record.getData()));
        return response;
    }

    private JsonNode parseJson(String jsonText) {
        if (jsonText == null || jsonText.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(jsonText);
        } catch (IOException ex) {
            throw new IllegalStateException("Invalid JSON in audit log payload", ex);
        }
    }
}
