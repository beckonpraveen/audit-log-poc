package org.example.helpdesk.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditLogWriter {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AuditLogWriter(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void write(
            String entityName,
            String entityId,
            String parentEntityName,
            String parentEntityId,
            AuditOperation operation,
            String actor,
            Map<String, Object> data
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO audit_log (
                    entity_name,
                    entity_id,
                    parent_entity_name,
                    parent_entity_id,
                    operation,
                    changed_at,
                    actor,
                    data
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                entityName,
                entityId,
                parentEntityName,
                parentEntityId,
                operation.name(),
                Timestamp.from(Instant.now()),
                actor,
                toJson(data)
        );
    }

    private String toJson(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize audit payload", ex);
        }
    }
}
