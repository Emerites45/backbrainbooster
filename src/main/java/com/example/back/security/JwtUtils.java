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
}