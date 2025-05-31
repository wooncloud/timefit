package org.example.timefitback.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.BUSINESS;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // 낙관적 락을 위한 버전 필드
    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public enum UserRole {
        USER("일반 사용자"),
        BUSINESS("사업자");

        private final String description;

        UserRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


}