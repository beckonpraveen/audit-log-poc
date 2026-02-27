package org.example.helpdesk.audit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;

public class AuditLogResponse {

    private Long id;
    private String entityName;
    private String entityId;
    private String parentEntityName;
    private String parentEntityId;
    private String operation;
    private Instant changedAt;
    private String actor;
    private JsonNode data;

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

    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}
