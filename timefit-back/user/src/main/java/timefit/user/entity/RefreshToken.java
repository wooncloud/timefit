package timefit.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Refresh Token 엔티티
 *
 * Phase 2-1: Refresh Token Rotation
 *
 * 목적:
 * - Refresh Token 재사용 감지
 * - 강제 로그아웃 구현
 * - 토큰 수명 관리
 *
 * Rotation 전략:
 * - 한 번 사용된 Refresh Token은 무효화
 * - 재사용 감지 시 모든 토큰 무효화 (보안 조치)
 */
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_jti", columnList = "jti"),
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("Refresh Token 추적 테이블")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Refresh Token ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    @Comment("JWT ID (고유 식별자)")
    private String jti;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    @Comment("사용자 ID")
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    @Comment("토큰 만료 시간")
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    @Comment("토큰 무효화 여부 (재사용 감지 또는 로그아웃)")
    private Boolean isRevoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("토큰 발급 시간")
    private LocalDateTime createdAt;

    /**
     * Private 생성자 (정적 팩토리 메서드로만 생성)
     */
    private RefreshToken(String jti, UUID userId, LocalDateTime expiresAt) {
        this.jti = jti;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.isRevoked = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 토큰 무효화 (Rotation 또는 로그아웃)
     */
    public void revoke() {
        this.isRevoked = true;
    }

    /**
     * 토큰 만료 여부 확인
     *
     * @return 만료되었으면 true
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * 토큰 유효성 확인
     *
     * @return 유효하면 true
     */
    public boolean isValid() {
        return !this.isRevoked && !this.isExpired();
    }

    /**
     * 정적 팩토리 메서드
     *
     * @param jti JWT ID
     * @param userId 사용자 ID
     * @param expiresAt 만료 시간
     * @return RefreshToken 인스턴스
     */
    public static RefreshToken of(String jti, UUID userId, LocalDateTime expiresAt) {
        return new RefreshToken(jti, userId, expiresAt);
    }
}