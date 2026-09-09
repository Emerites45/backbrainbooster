package com.example.back.controller;

import com.example.back.dto.request.ChangePasswordRequest;
import com.example.back.dto.request.LoginRequest;
import com.example.back.dto.request.ResetPasswordRequest;
import com.example.back.dto.request.SignupRequest;
import com.example.back.dto.response.AuthResponse;
import com.example.back.dto.response.MeResponse;
import com.example.back.exception.ResourceNotFoundException;
import com.example.back.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST d'authentification.
 * Routes publiques : signup / login / forgot / reset.
 * Routes JWT : me / change-password.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Inscription, login, profil et changement de mot de passe")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Inscription")
    public ResponseEntity<String> register(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            String message = authService.registerUser(signupRequest);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion — retourne un JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Demande OTP reset password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            String message = authService.forgotPassword(email);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password via OTP")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            String message = authService.resetPassword(request);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l’utilisateur connecté")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MeResponse> me() {
        return ResponseEntity.ok(authService.getMe(currentEmail()));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Changer le mot de passe (JWT requis)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(currentEmail(), request));
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof String email) || email.isBlank()) {
            throw new ResourceNotFoundException("Authenticated user required");
        }
        return email;
    }
}
