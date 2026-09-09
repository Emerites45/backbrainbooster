package com.example.back.dto.request;

import jakarta.validation.constraints.Email;
import java.util.List;
import java.util.Set;

public class UpdateUserRequest {

    private String name;
    private String firstName;
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    private String role;
    private Set<String> globalRoles;
    private List<DepartmentRoleRequest> departmentRoles;
    private Boolean mustChangePassword;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<String> getGlobalRoles() { return globalRoles; }
    public void setGlobalRoles(Set<String> globalRoles) { this.globalRoles = globalRoles; }

    public List<DepartmentRoleRequest> getDepartmentRoles() { return departmentRoles; }
    public void setDepartmentRoles(List<DepartmentRoleRequest> departmentRoles) { this.departmentRoles = departmentRoles; }

    public Boolean getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(Boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
}