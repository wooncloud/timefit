package timefit.invitation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.business.entity.Business;
import timefit.common.entity.BaseEntity;
import timefit.common.entity.BusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 업체 구성원 초대 엔티티
 * 핵심 기능:
 * - 업체가 이메일로 구성원 초대
 * - 토큰 기반 초대 링크 생성
 * - 초대 상태 관리 (PENDING/ACCEPTED/EXPIRED/CANCELED)
 * - 역할(role) 지정하여 초대
 */
@Entity
@Table(name = "invitation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation extends BaseEntity {

    /**
     * 초대 대상 업체
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    /**
     * 초대받을 이메일 주소
     * 소문자로 정규화하여 저장
     */
    @NotBlank
    @Email
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * 초대 토큰 (UUID)
     * 초대 링크에 사용되는 고유 식별자
     * 보안: 추측 불가능한 UUID 사용
     */
    @NotBlank
    @Size(max = 36)
    @Column(name = "token", nullable = false, unique = true, length = 36)
    private String token;

    /**
     * 초대 상태
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    /**
     * 초대받은 사람이 수락 시 부여될 역할
     * OWNER/MANAGER/MEMBER 중 하나
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private BusinessRole role;

    /**
     * 초대를 발송한 사용자
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    /**
     * 초대 만료 시각
     */
    @NotNull
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 초대 수락 시각
     * 수락 전에는 null
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // ========== Static Factory Methods ==========

    /**
     * 초대 생성 (기본 1일 만료)
     */
    public static Invitation create(
            Business business,
            String email,
            User invitedBy,
            BusinessRole role) {

        return create(business, email, invitedBy, role, 1);
    }

    /**
     * 초대 생성 (커스텀 만료 기간)
     */
    public static Invitation create(
            Business business,
            String email,
            User invitedBy,
            BusinessRole role,
            int expirationDays) {

        Invitation invitation = new Invitation();
        invitation.business = business;
        invitation.email = email.toLowerCase();
        invitation.token = UUID.randomUUID().toString();
        invitation.status = InvitationStatus.PENDING;
        invitation.invitedBy = invitedBy;
        invitation.role = role;
        invitation.expiresAt = LocalDateTime.now().plusDays(expirationDays);

        return invitation;
    }

    // ========== Business Methods ==========

    /**
     * 초대 수락
     */
    public void accept() {
        validatePending();
        this.status = InvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * 초대 취소
     */
    public void cancel() {
        validatePending();
        this.status = InvitationStatus.CANCELED;
    }

    /**
     * 초대 만료 처리
     */
    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }

    /**
     * 초대 만료 시간 연장 (재발송 시 사용)
     * 현재 시각 기준으로 1일 연장
     */
    public void extendExpiration() {
        validatePending();
        this.expiresAt = LocalDateTime.now().plusDays(1);
    }

    /**
     * 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * 유효한 초대인지 확인 (PENDING + 미만료)
     */
    public boolean isValid() {
        return this.status.isPending() && !isExpired();
    }

    // ========== Private Validation ==========

    private void validatePending() {
        if (!this.status.isPending()) {
            throw new IllegalStateException("PENDING 상태의 초대만 처리할 수 있습니다");
        }
    }
}