import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    private String status;

    private String messageId;

    private LocalDateTime sentAt;

    public EmailLog() {}

    public EmailLog(String recipient, EmailType emailType, String status, String messageId, LocalDateTime sentAt) {
        this.recipient = recipient;
        this.emailType = emailType;
        this.status = status;
        this.messageId = messageId;
        this.sentAt = sentAt;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public EmailType getEmailType() { return emailType; }
    public String getStatus() { return status; }
    public String getMessageId() { return messageId; }
    public LocalDateTime getSentAt() { return sentAt; }
}