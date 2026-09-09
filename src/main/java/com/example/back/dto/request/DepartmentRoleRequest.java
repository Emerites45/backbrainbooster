package com.example.back.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DepartmentRoleRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotBlank(message = "Role is required")
    private String role;

    public DepartmentRoleRequest() {}

    public DepartmentRoleRequest(Long departmentId, String role) {
        this.departmentId = departmentId;
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}