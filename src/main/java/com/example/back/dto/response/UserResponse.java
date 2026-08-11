package com.example.back.dto.response;

import com.example.back.model.UserStatus;
import java.time.ZonedDateTime;

public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final UserStatus status;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime deletedAt;

    public UserResponse(
            Long id,
            String name,
            String email,
            String role,
            UserStatus status,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            ZonedDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
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

    public UserStatus getStatus() {
        return status;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }
}
