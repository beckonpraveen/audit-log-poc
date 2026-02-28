package org.example.helpdesk.audit.service;

import java.util.List;
import org.example.helpdesk.audit.dto.AuditLogResponse;

public interface AuditLogService {
    List<AuditLogResponse> findByTicketId(Long ticketId);

    List<AuditLogResponse> findAdminAuditLogs(String entityName);
}
