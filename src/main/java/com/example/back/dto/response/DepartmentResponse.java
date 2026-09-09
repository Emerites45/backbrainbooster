package com.example.back.dto.response;

import java.time.ZonedDateTime;

public class DepartmentResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final boolean active;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public DepartmentResponse(
            Long id,
            String name,
            String description,
            boolean active,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}
