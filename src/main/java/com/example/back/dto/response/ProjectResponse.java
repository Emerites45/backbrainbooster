package com.example.back.dto.response;

import com.example.back.model.ProjectStatus;
import java.time.ZonedDateTime;

public class ProjectResponse {

    private final Long id;
    private final Long departmentId;
    private final String name;
    private final String description;
    private final ProjectStatus status;
    private final Long createdById;
    private final String createdByName;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime deletedAt;

    public ProjectResponse(
            Long id,
            Long departmentId,
            String name,
            String description,
            ProjectStatus status,
            Long createdById,
            String createdByName,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt,
            ZonedDateTime deletedAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.createdById = createdById;
        this.createdByName = createdByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
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
