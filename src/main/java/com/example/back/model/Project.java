package com.example.back.model;

import com.example.back.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Agrégat PROJECT (MCD). Soft-delete via {@code deleted_at}.
 */
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deletedAt;

    protected Project() {
    }

    public Project(Department department, String name, String description, User createdBy) {
        if (department == null || createdBy == null) {
            throw new IllegalArgumentException("department and createdBy are required");
        }
        this.department = department;
        this.name = requireName(name);
        this.description = description;
        this.createdBy = createdBy;
        this.status = ProjectStatus.ACTIVE;
    }

    public Department getDepartment() {
        return department;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void changeStatus(ProjectStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
        this.status = status;
    }

    public void softDelete() {
        if (this.deletedAt == null) {
            this.deletedAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }

    public void restore() {
        this.deletedAt = null;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        return name.trim();
    }
}
