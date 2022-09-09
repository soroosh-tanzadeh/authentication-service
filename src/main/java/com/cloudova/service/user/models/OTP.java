package com.cloudova.service.user.models;

import com.cloudova.service.config.BaseModel;
import com.cloudova.service.user.exceptions.InvalidOtpException;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "otps")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable(false)
public class OTP extends BaseModel {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Builder.Default()
    private UUID verificationToken = UUID.randomUUID();

    @Column
    private String identifier;

    @Column(unique = true)
    private String token;

    @Column
    private boolean isVerified;

    @Column
    private Timestamp verifiedAt;

    @Column
    @Builder.Default
    private Timestamp expiresOn = Timestamp.valueOf(LocalDateTime.now().plus(2, ChronoUnit.MINUTES));

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OTP otp = (OTP) o;
        return id != null && Objects.equals(id, otp.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (isVerified ? 1 : 0);
        result = 31 * result + (verifiedAt != null ? verifiedAt.hashCode() : 0);
        result = 31 * result + (expiresOn != null ? expiresOn.hashCode() : 0);
        return result;
    }

    public void verify() {
        if (this.isVerified) {
            throw new InvalidOtpException("Invalid otp");
        }
        this.setVerified(true);
        this.setVerifiedAt(Timestamp.valueOf(LocalDateTime.now()));
    }
}
