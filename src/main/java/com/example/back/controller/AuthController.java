package com.example.back.controller;

import com.example.back.dto.request.LoginRequest;
import com.example.back.dto.request.SignupRequest;
import com.example.back.dto.response.AuthResponse;
import com.example.back.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST gérant les requêtes HTTP d'authentification.
 * Expose les points d'entrée (endpoints) pour l'inscription et la connexion des
 * utilisateurs.
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*") // Permet d'éviter les blocages CORS classiques durant le développement avec le
                            // Front
public class AuthController {

    private final AuthService authService;

    // Injection du service d'authentification
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint permettant l'inscription d'un nouvel utilisateur.
     * Route : POST /api/v1/auth/signup
     *
     * @param signupRequest Contient le nom, l'email et le mot de passe validés.
     * @return Un message de confirmation sous forme de chaîne de caractères.
     */
    @PostMapping("/signup")
    public ResponseEntity<String> register(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            String message = authService.registerUser(signupRequest);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Renvoie une erreur 400 (Bad Request) si l'email existe déjà par exemple
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint permettant la connexion d'un utilisateur existant.
     * Route : POST /api/v1/auth/login
     *
     * @param loginRequest Contient les identifiants de connexion (email et mot de
     *                     passe).
     * @return Une réponse contenant l'objet AuthResponse (Token JWT inclus) si
     *         l'authentification réussit.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Renvoie une erreur 401 (Unauthorized) si les identifiants sont invalides
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}