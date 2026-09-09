package com.example.back.dto.response;

import java.time.ZonedDateTime;

/** Membre d’un département (sans secrets). */
public class DepartmentUserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final ZonedDateTime createdAt;

    public DepartmentUserResponse(
            Long id, String name, String email, String role, ZonedDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
