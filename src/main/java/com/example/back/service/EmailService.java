package com.example.back.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service générique et réutilisable pour l'envoi d'e-mails HTML via Brevo SMTP.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String defaultFromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un e-mail au format HTML de manière asynchrone (en arrière-plan).
     *
     * @param to          L'adresse email du destinataire.
     * @param subject     L'objet du message.
     * @param htmlContent Le corps du message au format HTML.
     */
    @Async
    @SuppressWarnings("null")
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (to == null || subject == null || htmlContent == null) {
            System.err.println("Échec de l'envoi d'email : destinataire, objet ou contenu nul.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromAddress = (defaultFromEmail != null && !defaultFromEmail.isBlank())
                    ? defaultFromEmail
                    : "noreply@brainbooster.com";

            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("E-mail envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.err.println("Échec de l'envoi de l'e-mail à " + to + " : " + e.getMessage());
        }
    }

    public void generateAndSendOtp(String email) {
    // ... logique d'envoi OTP
}
}