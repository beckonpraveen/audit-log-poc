package org.example.helpdesk.audit.entity;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_log")
public class AuditLogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "parent_entity_name")
    private String parentEntityName;

    @Column(name = "parent_entity_id")
    private String parentEntityId;

    @Column(nullable = false)
    private String operation;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(nullable = false)
    private String actor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "json")
    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getParentEntityName() {
        return parentEntityName;
    }

    public void setParentEntityName(String parentEntityName) {
        this.parentEntityName = parentEntityName;
    }

    public String getParentEntityId() {
        return parentEntityId;
    }

    public void setParentEntityId(String parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
