package com.example.back.security;

import com.example.back.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Composant utilitaire pour la gestion des jetons JSON Web Tokens (JWT).
 * En charge de la génération, de la lecture et de la validation des jetons de
 * session.
 */
@Component
public class JwtUtils {

    // Récupère la clé secrète configurée dans application.properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKeyString;

    // Récupère la durée de validité configurée dans application.properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Génère un jeton JWT crypté pour un utilisateur fraîchement connecté.
     *
     * @param user L'entité utilisateur authentifiée.
     * @return Une chaîne de caractères représentant le JWT compacté.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(user.getEmail()) // L'identifiant principal est l'email
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Extrait l'email (subject) contenu dans le token.
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrait le rôle contenu dans le token.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Vérifie que le token est valide : signature correcte et non expiré.
     */
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
