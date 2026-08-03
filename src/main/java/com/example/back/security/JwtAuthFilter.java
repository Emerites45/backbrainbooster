package com.example.back.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtre exécuté à chaque requête authentifiée : lit le header Authorization, 
 * valide le JWT, et authentifie l'utilisateur dans le contexte de sécurité.
*/
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Définit les routes et méthodes HTTP publiques qui NE DOIVENT PAS passer par le filtre JWT.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        // Ignorer systématiquement les requêtes Preflight CORS (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getServletPath();
        
        // Sécurité supplémentaire si getServletPath() retourne une chaîne vide ou null
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }

        // Routes publiques (Auth, Swagger, Emails public endpoints)
        return path.startsWith("/api/v1/auth/") ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/api/emails/") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtils.validateToken(token)) {
                    String email = jwtUtils.extractEmail(token);
                    String role = jwtUtils.extractRole(token);

                    // Formattage des autorités avec le préfixe Spring standard ROLE_
                    var authorities = (role != null && !role.isBlank())
                            ? List.of(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                            : List.<SimpleGrantedAuthority>of();

                    var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

                    // Injection de l'utilisateur authentifié dans le contexte Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // En cas d'erreur de parsing du token, on nettoie le contexte
            SecurityContextHolder.clearContext();
        }

        // Poursuite de la chaîne de filtres Spring
        filterChain.doFilter(request, response);
    }
}