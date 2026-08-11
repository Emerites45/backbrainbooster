package com.example.back.model;

/**
 * Statut compte utilisateur exposé par l’API (ACTIVE ↔ deleted_at null).
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE;

    public static UserStatus fromUser(User user) {
        return user.isActive() ? ACTIVE : INACTIVE;
    }
}
