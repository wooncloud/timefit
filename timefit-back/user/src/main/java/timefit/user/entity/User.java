package timefit.user.entity;

import timefit.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

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

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;



    /**
     * 업체 사용자 생성 (이메일 회원가입)
     */
    public static User createBusinessUser(String email, String password, String name, String phoneNumber) {
        User user = new User();
        user.email = email;
        user.passwordHash = password; // 실제로는 암호화 필요
        user.name = name;
        user.phoneNumber = phoneNumber;
        user.role = UserRole.BUSINESS;
        user.lastLoginAt = LocalDateTime.now();
        return user;
    }

    /**
     * OAuth 고객 사용자 생성
     */
    public static User createOAuthUser(String email, String name, String profileImageUrl,
                                       String oauthProvider, String oauthId) {
        User user = new User();
        user.email = email;
        user.name = name;
        user.profileImageUrl = profileImageUrl;
        user.role = UserRole.USER;
        user.oauthProvider = oauthProvider;
        user.oauthId = oauthId;
        user.lastLoginAt = LocalDateTime.now();
        return user;
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 사용자 정보 업데이트
     */
    public void updateUserInfo(String name, String phoneNumber, String profileImageUrl) {
        if (name != null) {
            this.name = name;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}

