package com.example.back.config;

import java.util.List;

import com.example.back.security.JwtAuthFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration principale de la sécurité de l'application.
 * Définit les règles d'accès aux endpoints de l'API et configure l'encodeur de
 * mots de passe.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Bean responsable du hachage sécurisé des mots de passe des utilisateurs via
     * l'algorithme BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Autorise le frontend React (localhost:5173) à appeler l'API depuis le navigateur.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Définit la chaîne de filtres de sécurité pour filtrer les requêtes HTTP
     * entrantes.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Active la config CORS définie ci-dessus
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Désactivation du CSRF car l'API utilise des tokens stateless (JWT)
                .csrf(csrf -> csrf.disable())
                // Configuration des règles d'accès aux URLs
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques d'authentification
                        .requestMatchers("/api/auth/**", "/api/v1/auth/**").permitAll()
                        // Autorisation d'accès aux endpoints d'emails (OTP, Welcome, etc.)
                        .requestMatchers("/api/emails/**").permitAll()
                        // Autorisation d'accès à la documentation Swagger UI & OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Toutes les autres requêtes nécessiteront une authentification JWT
                        .anyRequest().authenticated())
                // Politique de session Stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Insère notre filtre JWT avant le filtre standard de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
