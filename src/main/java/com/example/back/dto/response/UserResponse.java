package com.example.back.dto.response;

import com.example.back.model.UserStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Set<String> globalRoles;
    private List<DepartmentRoleResponse> departmentRoles;
 private Boolean mustChangePassword;
    private UserStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<DepartmentRoleResponse> getDepartmentRoles() { return departmentRoles; }
    public void setDepartmentRoles(List<DepartmentRoleResponse> departmentRoles) { this.departmentRoles = departmentRoles; }

    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public ZonedDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }

   

public Boolean getMustChangePassword() { 
    return mustChangePassword; 
}

public void setMustChangePassword(Boolean mustChangePassword) { 
    this.mustChangePassword = mustChangePassword; 
}
}