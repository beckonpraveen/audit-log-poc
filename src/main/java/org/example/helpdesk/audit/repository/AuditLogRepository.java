package org.example.helpdesk.audit.repository;

import java.util.Collection;
import java.util.List;
import org.example.helpdesk.audit.entity.AuditLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {
    @Query("""
            SELECT a
            FROM AuditLogRecord a
            WHERE (a.entityName = :entityName AND a.entityId = :ticketId)
               OR (a.parentEntityName = :entityName AND a.parentEntityId = :ticketId)
            """)
    List<AuditLogRecord> findForEntityOrParent(
            @Param("entityName") String entityName,
            @Param("ticketId") String ticketId
    );

    List<AuditLogRecord> findByEntityNameIn(Collection<String> entityNames);

    List<AuditLogRecord> findByEntityName(String entityName);
}
