package com.example.back.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_app")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "nom", nullable = false)
    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Deprecated
    @Column(name = "role")
    private String role = "USER";

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_global_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> globalRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserDepartmentRole> departmentRoles = new ArrayList<>();

   @Column(name = "must_change_password")
private Boolean mustChangePassword = false;



    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiry", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime passwordResetTokenExpiry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "date_modification", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime updatedAt;

    @Column(name = "date_suppression", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime deletedAt;

    public User() {}

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(String firstName, String lastName, String email, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        updateFullName();
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Set<String> getGlobalRoles() { return globalRoles; }
    public void setGlobalRoles(Set<String> globalRoles) { this.globalRoles = globalRoles; }

    public List<UserDepartmentRole> getDepartmentRoles() { return departmentRoles; }
    public void setDepartmentRoles(List<UserDepartmentRole> departmentRoles) { this.departmentRoles = departmentRoles; }


    public Boolean getMustChangePassword() {
    return mustChangePassword != null && mustChangePassword;
}

public boolean isMustChangePassword() {
    return mustChangePassword != null && mustChangePassword;
}
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    public String getPasswordResetToken() { return passwordResetToken; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }

    public ZonedDateTime getPasswordResetTokenExpiry() { return passwordResetTokenExpiry; }
    public void setPasswordResetTokenExpiry(ZonedDateTime passwordResetTokenExpiry) { this.passwordResetTokenExpiry = passwordResetTokenExpiry; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public ZonedDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(ZonedDateTime deletedAt) { this.deletedAt = deletedAt; }

    // --- MÉTHODES MÉTIER ---
    public boolean isActive() { return deletedAt == null; }

    public void deactivate() {
        if (this.deletedAt == null) {
            this.deletedAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }

    public void activate() { this.deletedAt = null; }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = newName.trim();
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        this.email = newEmail.trim().toLowerCase();
    }

    public void assignRole(String newRole) {
        if (newRole == null || newRole.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        String normalized = newRole.trim().toUpperCase();
        this.role = normalized;
        if (!this.globalRoles.contains(normalized)) {
            this.globalRoles.add(normalized);
        }
    }

    public void addGlobalRole(String role) {
        if (role != null && !role.isBlank()) {
            this.globalRoles.add(role.trim().toUpperCase());
        }
    }

    public void removeGlobalRole(String role) {
        this.globalRoles.remove(role);
    }

    public void addDepartmentRole(Department department, String role) {
        if (department == null || !department.isActive()) {
            throw new IllegalArgumentException("Department must be valid and active");
        }
        boolean exists = this.departmentRoles.stream()
                .anyMatch(udr -> udr.getDepartment().getId().equals(department.getId())
                        && udr.getRole().equalsIgnoreCase(role));
        if (!exists) {
            this.departmentRoles.add(new UserDepartmentRole(this, department, role));
        }
    }

    public void removeDepartmentRole(Long departmentId, String role) {
        this.departmentRoles.removeIf(udr ->
            udr.getDepartment().getId().equals(departmentId)
            && udr.getRole().equalsIgnoreCase(role)
        );
    }

    public void clearDepartmentRoles() {
        this.departmentRoles.clear();
    }

    private void updateFullName() {
        StringBuilder sb = new StringBuilder();
        if (this.firstName != null) sb.append(this.firstName);
        if (this.lastName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(this.lastName);
        }
        this.name = sb.toString();
    }
}