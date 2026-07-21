package com.example.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration principale de la sécurité de l'application.
 * Définit les règles d'accès aux endpoints de l'API et configure l'encodeur de
 * mots de passe.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean responsable du hachage sécurisé des mots de passe des utilisateurs via
     * l'algorithme BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définit la chaîne de filtres de sécurité pour filtrer les requêtes HTTP
     * entrantes.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation du CSRF car l'API utilise des tokens stateless (JWT)
                .csrf(csrf -> csrf.disable())

                // Configuration des règles d'accès aux URLs
                .authorizeHttpRequests(auth -> auth
                        // On autorise explicitement toutes les variantes de routes d'authentification
                        .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()
                        // Toutes les autres requêtes nécessiteront une authentification
                        .anyRequest().authenticated())

                // Politique de session Stateless (aucune session HTTP côté serveur, tout passe
                // par le JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}