package com.example.back.dto.response;

import java.time.ZonedDateTime;

public class ActionHistoryResponse {

    private final Long id;
    private final String entityType;
    private final Long entityId;
    private final String actionType;
    private final String fieldName;
    private final String oldValue;
    private final String newValue;
    private final Long performedById;
    private final String performedByName;
    private final ZonedDateTime performedAt;

    public ActionHistoryResponse(
            Long id,
            String entityType,
            Long entityId,
            String actionType,
            String fieldName,
            String oldValue,
            String newValue,
            Long performedById,
            String performedByName,
            ZonedDateTime performedAt) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actionType = actionType;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedById = performedById;
        this.performedByName = performedByName;
        this.performedAt = performedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getActionType() {
        return actionType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Long getPerformedById() {
        return performedById;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public ZonedDateTime getPerformedAt() {
        return performedAt;
    }
}
