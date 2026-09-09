package com.example.back.model;

import com.example.back.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_department_role")
public class UserDepartmentRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    protected UserDepartmentRole() {
        // JPA
    }

    public UserDepartmentRole(User user, Department department, String role) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department is required");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        this.user = user;
        this.department = department;
        this.role = role.trim().toUpperCase();
    }

    public User getUser() {
        return user;
    }

    public Department getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }

    public void updateRole(String newRole) {
        if (newRole == null || newRole.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        this.role = newRole.trim().toUpperCase();
    }
}