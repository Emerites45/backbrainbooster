package com.example.back.dto.response;

public class DepartmentRoleResponse {

    private Long departmentId;
    private String departmentName;
    private String role;

    public DepartmentRoleResponse() {}

    public DepartmentRoleResponse(Long departmentId, String departmentName, String role) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}