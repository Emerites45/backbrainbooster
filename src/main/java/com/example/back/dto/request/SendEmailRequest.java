import com.example.back.model.EmailType;
import io.swagger.v3.oas.annotations.media.Schema;

public class SendEmailRequest {

    @Schema(description = "Adresse email du destinataire", example = "utilisateur@example.com")
    private String toEmail;

    @Schema(description = "Nom du destinataire", example = "Jean Dupont")
    private String recipientName;

    @Schema(description = "Type d'email à envoyer", example = "OTP")
    private EmailType emailType;

    @Schema(description = "Code OTP (requis si emailType = OTP)", example = "482901")
    private String otpCode;

    @Schema(description = "Lien de réinitialisation (requis si emailType = PASSWORD_RESET)", example = "https://app.brainbooster.com/reset-password?token=xyz123")
    private String resetLink;

    // Getters et Setters
    public String getToEmail() { return toEmail; }
    public void setToEmail(String toEmail) { this.toEmail = toEmail; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public EmailType getEmailType() { return emailType; }
    public void setEmailType(EmailType emailType) { this.emailType = emailType; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getResetLink() { return resetLink; }
    public void setResetLink(String resetLink) { this.resetLink = resetLink; }
}