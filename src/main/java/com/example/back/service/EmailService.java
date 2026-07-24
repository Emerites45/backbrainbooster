package com.example.back.service;

import com.example.back.model.EmailLog;
import com.example.back.model.EmailType;
import com.example.back.repository.EmailLogRepository;
import com.example.back.util.OtpGeneratorUtil;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateSmtpEmail;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class EmailService {

    private final EmailLogRepository emailLogRepository;
    private final OtpGeneratorUtil otpGeneratorUtil;
    private final String senderEmail;
    private final String senderName;
    private final TransactionalEmailsApi apiInstance;

    public EmailService(
            EmailLogRepository emailLogRepository,
            OtpGeneratorUtil otpGeneratorUtil,
            @Value("${brevo.api.key}") String apiKey,
            @Value("${brevo.sender.email}") String senderEmail,
            @Value("${brevo.sender.name}") String senderName) {
        
        this.emailLogRepository = emailLogRepository;
        this.otpGeneratorUtil = otpGeneratorUtil;
        this.senderEmail = senderEmail;
        this.senderName = senderName;

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        this.apiInstance = new TransactionalEmailsApi();
    }

    public void sendEmail(String toEmail, String recipientName, String subject, String htmlContent, EmailType emailType) {
        try {
            SendSmtpEmailSender sender = new SendSmtpEmailSender();
            sender.setEmail(senderEmail);
            sender.setName(senderName);

            SendSmtpEmailTo to = new SendSmtpEmailTo();
            to.setEmail(toEmail);
            to.setName(recipientName != null ? recipientName : toEmail);

            SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
            sendSmtpEmail.setSender(sender);
            sendSmtpEmail.setTo(Collections.singletonList(to));
            sendSmtpEmail.setSubject(subject);
            sendSmtpEmail.setHtmlContent(htmlContent);

            CreateSmtpEmail result = apiInstance.sendTransacEmail(sendSmtpEmail);

            EmailLog log = new EmailLog(toEmail, emailType, "SUCCESS", result.getMessageId(), LocalDateTime.now());
            emailLogRepository.save(log);

        } catch (ApiException e) {
            EmailLog log = new EmailLog(toEmail, emailType, "FAILED: " + e.getMessage(), null, LocalDateTime.now());
            emailLogRepository.save(log);
            throw new RuntimeException("Échec d'envoi via Brevo : " + e.getResponseBody(), e);
        }
    }

    /**
     * Génère un OTP à 6 chiffres et l'envoie à l'utilisateur.
     * @return Le code OTP généré pour pouvoir le sauvegarder/vérifier côté backend.
     */
    public String generateAndSendOtp(String toEmail) {
        // Génération automatique du code à 6 chiffres
        String generatedOtp = otpGeneratorUtil.generateOtp(6);

        String subject = "Votre code de vérification - Brain-Booster";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #2c3e50;">Vérification de votre compte</h2>
                <p>Voici votre code de vérification :</p>
                <p style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #27ae60; text-align: center;">%s</p>
                <p>Ce code expire dans 5 minutes.</p>
                <p style="font-size: 12px; color: #7f8c8d;">Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>
            </div>
            """.formatted(generatedOtp);

        sendEmail(toEmail, null, subject, htmlContent, EmailType.OTP);
        
        return generatedOtp; // Retourne le code généré au contrôleur ou service d'authentification
    }

    public void sendWelcomeEmail(String toEmail, String recipientName) {
        String subject = "Bienvenue sur Brain-Booster !";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px;">
                <h2>Bienvenue %s ! 🎉</h2>
                <p>Votre compte a été créé avec succès.</p>
            </div>
            """.formatted(recipientName != null ? recipientName : "");

        sendEmail(toEmail, recipientName, subject, htmlContent, EmailType.WELCOME);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Réinitialisation de votre mot de passe";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px;">
                <h2>Réinitialisation du mot de passe</h2>
                <p>Cliquez sur le bouton ci-dessous pour changer votre mot de passe :</p>
                <p><a href="%s" style="background-color: #2980b9; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Réinitialiser</a></p>
            </div>
            """.formatted(resetLink);

        sendEmail(toEmail, null, subject, htmlContent, EmailType.PASSWORD_RESET);
    }
}