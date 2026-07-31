package com.example.back.service;

import org.springframework.stereotype.Component;

/**
 * Classe utilitaire contenant les templates HTML pour les différents e-mails de
 * l'application.
 */
@Component
public class EmailTemplates {

    /**
     * Génère le corps HTML pour l'e-mail de confirmation d'inscription.
     */
    public String buildWelcomeEmail(String userName) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .card { background-color: #ffffff; padding: 30px; border-radius: 10px; max-width: 550px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                        .header { text-align: center; color: #4F46E5; margin-bottom: 20px; }
                        .content { color: #333333; line-height: 1.6; font-size: 16px; }
                        .button-container { text-align: center; margin-top: 25px; }
                        .button { background-color: #4F46E5; color: #ffffff !important; padding: 12px 25px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; }
                        .footer { font-size: 12px; color: #888888; text-align: center; margin-top: 30px; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1 class="header">Bienvenue sur Brain-Booster ! 🚀</h1>
                        <div class="content">
                            <p>Bonjour <strong>%s</strong>,</p>
                            <p>Toute l'équipe est ravie de vous compter parmi nous. Votre compte a été créé avec succès.</p>
                            <p>Vous pouvez désormais vous connecter et commencer à booster votre productivité dès aujourd'hui !</p>
                        </div>
                        <div class="button-container">
                            <a href="http://localhost:3000/login" class="button">Accéder à mon espace</a>
                        </div>
                        <div class="footer">
                            <p>Cet e-mail a été envoyé automatiquement par Brain-Booster.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName);
    }

    public String buildPasswordResetEmail(String userName, String otp) {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .card { background-color: #ffffff; padding: 30px; border-radius: 10px; max-width: 550px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                        .header { text-align: center; color: #4F46E5; margin-bottom: 20px; }
                        .content { color: #333333; line-height: 1.6; font-size: 16px; }
                        .otp { display: inline-block; padding: 12px 20px; background-color: #f0f4ff; border-radius: 8px; font-size: 24px; letter-spacing: 4px; font-weight: bold; margin: 20px 0; }
                        .footer { font-size: 12px; color: #888888; text-align: center; margin-top: 30px; }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <h1 class="header">Réinitialisation du mot de passe</h1>
                        <div class="content">
                            <p>Bonjour <strong>%s</strong>,</p>
                            <p>Vous avez demandé à réinitialiser votre mot de passe. Utilisez le code ci-dessous pour confirmer votre identité :</p>
                            <div class="otp">%s</div>
                            <p>Ce code est valable pendant 15 minutes.</p>
                            <p>Si vous n'êtes pas à l'origine de cette demande, ignorez simplement cet e-mail.</p>
                        </div>
                        <div class="footer">
                            <p>Cet e-mail a été envoyé automatiquement par Brain-Booster.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(userName, otp);
    }
}