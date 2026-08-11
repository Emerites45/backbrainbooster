package com.example.back.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Tous les champs optionnels — seuls les non-null sont appliqués. */
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @Email(message = "Le format de l'adresse email est invalide")
    private String email;

    @Pattern(regexp = "(?i)ADMIN|USER", message = "Role must be ADMIN or USER")
    private String role;

    public UpdateUserRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
