package com.example.back.controller;

import com.example.back.dto.SendEmailRequest;
import com.example.back.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@Tag(name = "Service d'Emailing Brevo", description = "Endpoints d'envoi d'emails transactionnels")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Générer et envoyer un code OTP", description = "Génère un code à 6 chiffres et l'envoie à l'email spécifié.")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        String generatedOtp = emailService.generateAndSendOtp(email);
        // En situation réelle, sauvegardez cet OTP en BDD ou Redis pour valider la saisie utilisateur ultérieurement.
        return ResponseEntity.ok("OTP généré et envoyé à " + email + " (Code généré : " + generatedOtp + ")");
    }

    @PostMapping("/send")
    @Operation(summary = "Envoyer un email générique (Bienvenue / Reset Password)")
    public ResponseEntity<String> sendEmail(@RequestBody SendEmailRequest request) {
        if (request.getEmailType() == null) {
            return ResponseEntity.badRequest().body("Le type d'email est obligatoire.");
        }

        switch (request.getEmailType()) {
            case WELCOME:
                emailService.sendWelcomeEmail(request.getToEmail(), request.getRecipientName());
                break;

            case PASSWORD_RESET:
                if (request.getResetLink() == null || request.getResetLink().isBlank()) {
                    return ResponseEntity.badRequest().body("Le lien de réinitialisation est obligatoire.");
                }
                emailService.sendPasswordResetEmail(request.getToEmail(), request.getResetLink());
                break;

            default:
                return ResponseEntity.badRequest().body("Utilisez /send-otp pour générer et envoyer un OTP.");
        }

        return ResponseEntity.ok("Email de type " + request.getEmailType() + " envoyé avec succès.");
    }
}