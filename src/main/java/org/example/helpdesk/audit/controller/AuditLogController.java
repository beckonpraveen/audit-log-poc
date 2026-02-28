package org.example.helpdesk.audit.controller;

import java.util.List;
import org.example.helpdesk.audit.dto.AuditLogResponse;
import org.example.helpdesk.audit.service.AuditLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/api/tickets/{ticketId}/audit-logs")
    public List<AuditLogResponse> findByTicketId(@PathVariable Long ticketId) {
        return auditLogService.findByTicketId(ticketId);
    }

    @GetMapping("/api/admin/audit-logs")
    public List<AuditLogResponse> findAdminAuditLogs(@RequestParam(required = false) String entityName) {
        return auditLogService.findAdminAuditLogs(entityName);
    }
}
