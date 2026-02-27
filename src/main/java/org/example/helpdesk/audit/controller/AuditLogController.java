package org.example.helpdesk.audit.controller;

import java.util.List;
import org.example.helpdesk.audit.dto.AuditLogResponse;
import org.example.helpdesk.audit.service.AuditLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/{ticketId}/audit-logs")
    public List<AuditLogResponse> findByTicketId(@PathVariable Long ticketId) {
        return auditLogService.findByTicketId(ticketId);
    }
}
