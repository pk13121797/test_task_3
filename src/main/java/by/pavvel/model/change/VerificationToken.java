package by.pavvel.model.change;

import by.pavvel.model.reg.ApplicationUser;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "VerificationToken")
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @SequenceGenerator(name = "token_sequence",sequenceName = "token_sequence",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "token_sequence")
    @Column(name = "id",updatable = false)
    private Long id;

    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "created",nullable = false)
    private LocalDateTime created;

    @Column(name = "expired",nullable = false)
    private LocalDateTime expired;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "user_id",foreignKey = @ForeignKey(name = "application_user_fk"))
    private ApplicationUser applicationUser;

    public VerificationToken() {
    }

    public VerificationToken(String token, LocalDateTime created,
                             LocalDateTime expired, ApplicationUser applicationUser) {
        this.token = token;
        this.created = created;
        this.expired = expired;
        this.applicationUser = applicationUser;
    }
}
