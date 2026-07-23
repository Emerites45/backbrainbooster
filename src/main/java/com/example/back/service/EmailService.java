/*package com.example.back.service;

public class EmailService {
    
}*/

package com.example.back.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Votre code de vérification - Brain-Booster");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto;">
                    <h2>Vérification de votre compte</h2>
                    <p>Voici votre code de vérification :</p>
                    <p style="font-size: 28px; font-weight: bold; letter-spacing: 4px;">%s</p>
                    <p>Ce code expire dans 5 minutes.</p>
                    <p>Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.</p>
                </div>
                """.formatted(otpCode);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Échec de l'envoi de l'email OTP", e);
        }
    }
}
