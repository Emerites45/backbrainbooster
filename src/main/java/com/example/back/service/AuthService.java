package com.example.back.service;

import com.example.back.dto.request.LoginRequest;
import com.example.back.dto.request.ResetPasswordRequest;
import com.example.back.dto.request.SignupRequest;
import com.example.back.dto.response.AuthResponse;
import com.example.back.model.User;
import com.example.back.repository.UserRepository;
import com.example.back.security.JwtUtils;
import com.example.back.util.OtpGeneratorUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;

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
    private final EmailService emailService;
    private final EmailTemplates emailTemplates;
    private final OtpGeneratorUtil otpGeneratorUtil;
    

    // Injection par constructeur
    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            EmailService emailService,
            EmailTemplates emailTemplates,
            OtpGeneratorUtil otpGeneratorUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.emailTemplates = emailTemplates;
        this.otpGeneratorUtil = otpGeneratorUtil;
    }

    /**
     * Enregistre un nouvel utilisateur dans le système après vérification des
     * doublons.
     * Le mot de passe est haché et un e-mail de confirmation est envoyé.
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
                passwordEncoder.encode(request.getPassword()));

        // 3. Sauvegarde en base de données PostgreSQL
        User savedUser = userRepository.save(newUser);

        // 4. Envoi de l'e-mail de confirmation via Brevo
        String emailSubject = "Bienvenue sur Brain-Booster ! 🚀";
        String emailContent = emailTemplates.buildWelcomeEmail(savedUser.getName());
        emailService.sendHtmlEmail(savedUser.getEmail(), emailSubject, emailContent);

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
    @Transactional
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé avec cet email."));

        String otp = otpGeneratorUtil.generateOtp(6);
        user.setPasswordResetToken(otp);
        user.setPasswordResetTokenExpiry(ZonedDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String emailSubject = "Réinitialisation du mot de passe Brain-Booster";
        String emailContent = emailTemplates.buildPasswordResetEmail(user.getName(), otp);
        emailService.sendHtmlEmail(user.getEmail(), emailSubject, emailContent);

        return "Un lien de réinitialisation a été envoyé à votre adresse email.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur trouvé avec cet email."));

        if (user.getPasswordResetToken() == null || user.getPasswordResetTokenExpiry() == null) {
            throw new RuntimeException("Aucune demande de réinitialisation n'a été trouvée pour cet utilisateur.");
        }

        if (ZonedDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new RuntimeException("Le code de réinitialisation a expiré. Veuillez demander un nouveau code.");
        }

        if (!user.getPasswordResetToken().equals(request.getOtp())) {
            throw new RuntimeException("Code de réinitialisation invalide.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        return "Mot de passe réinitialisé avec succès.";
    }
}
