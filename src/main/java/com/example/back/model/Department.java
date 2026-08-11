package com.example.back.model;

import com.example.back.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Agrégat DEPARTMENT (MCD). Soft-delete = {@code active=false}.
 */
@Entity
@Table(name = "department")
public class Department extends BaseEntity {

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    protected Department() {
        // JPA
    }

    public Department(String name, String description) {
        this.name = requireName(name);
        this.description = description;
        this.active = true;
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

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Department name is required");
        }
        return name.trim();
    }
}
