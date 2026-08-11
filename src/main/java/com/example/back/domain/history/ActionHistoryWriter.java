package com.example.back.domain.history;

import com.example.back.model.User;

/**
 * Port d’écriture audit (OCP) — ACTION_HISTORY polymorphe.
 */
public interface ActionHistoryWriter {

    void write(
            String entityType,
            Long entityId,
            String actionType,
            String fieldName,
            String oldValue,
            String newValue,
            User performedBy);

    default void writeCreated(String entityType, Long entityId, User performedBy) {
        write(entityType, entityId, "CREATE", null, null, null, performedBy);
    }

    default void writeDeleted(String entityType, Long entityId, User performedBy) {
        write(entityType, entityId, "DELETE", "deleted_at", null, "SET", performedBy);
    }

    default void writeFieldChange(
            String entityType,
            Long entityId,
            String fieldName,
            String oldValue,
            String newValue,
            User performedBy) {
        write(entityType, entityId, "UPDATE", fieldName, oldValue, newValue, performedBy);
    }
}
