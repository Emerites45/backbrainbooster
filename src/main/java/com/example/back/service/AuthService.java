package com.example.back.service;

import com.example.back.dto.request.LoginRequest;
import com.example.back.dto.request.SignupRequest;
import com.example.back.dto.response.AuthResponse;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service gérant toute la logique métier liée à l'authentification des
 * utilisateurs.
 * Traite les inscriptions de nouveaux comptes et les validations de connexions.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Injection par constructeur (recommandée pour la testabilité)
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Enregistre un nouvel utilisateur dans le système après vérification des
     * doublons.
     * Le mot de passe est automatiquement haché via BCrypt pour des raisons de
     * sécurité.
     *
     * @param request Les données d'inscription envoyées par le Frontend.
     * @return Un message de confirmation de succès.
     * @throws RuntimeException Si l'adresse email est déjà utilisée.
     */
    @Transactional
    public String registerUser(SignupRequest request) {
        // 1. Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Erreur : Cet e-mail est déjà utilisé !");
        }

        // 2. Création de la nouvelle entité avec mot de passe haché
        User newUser = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()) // Hachage BCrypt
        );

        // 3. Sauvegarde en base de données PostgreSQL
        userRepository.save(newUser);

        return "Utilisateur enregistré avec succès !";
    }

    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     * Vérifie la concordance du mot de passe haché et génère son jeton d'accès JWT.
     *
     * @param request Les données de connexion (email et mot de passe).
     * @return L'objet {@link AuthResponse} contenant le jeton et les infos de
     *         l'utilisateur.
     * @throws RuntimeException Si les identifiants fournis sont erronés.
     */
    @Transactional(readOnly = true)
    public AuthResponse authenticateUser(LoginRequest request) {
        // 1. Recherche de l'utilisateur par e-mail
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Erreur : Identifiants incorrects !"));

        // 2. Vérification de la validité du mot de passe saisi avec celui stocké
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Erreur : Identifiants incorrects !");
        }

        // 3. Génération du Token JWT crypté
        String jwtToken = jwtUtils.generateToken(user);

        // 4. Renvoi de la payload attendue par le Frontend dans le corps du JSON
        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
}