package timefit.invitation.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.invitation.InvitationErrorCode;
import timefit.exception.invitation.InvitationException;
import timefit.invitation.entity.Invitation;
import timefit.invitation.repository.InvitationRepository;

import java.util.UUID;

/**
 * Invitation 검증 전담 클래스
 * - 구성원 여부 검증
 * - 초대 존재 및 유효성 검증
 * - 권한 및 소속 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationValidator {

    private final InvitationRepository invitationRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;

    /**
     * 이미 구성원인지 검증
     * - 해당 이메일로 이미 업체 구성원이 아닌지 확인
     * - 구성원은 초대 불가
     *
     * @param businessId 업체 ID
     * @param email 초대할 이메일
     * @throws InvitationException 이미 업체 구성원일 경우
     */
    public void validateNotAlreadyMember(UUID businessId, String email) {
        boolean isMember = userBusinessRoleRepository.existsByBusinessIdAndUserEmail(
                businessId,
                email.toLowerCase()
        );

        if (isMember) {
            log.warn("이미 구성원인 사용자 초대 시도: businessId={}, email={}", businessId, email);
            throw new InvitationException(InvitationErrorCode.ALREADY_MEMBER);
        }
    }

    /**
     * 초대 존재 여부 검증
     * - 초대 ID로 초대 조회
     * - 존재하지 않으면 예외 발생
     *
     * @param invitationId 초대 ID
     * @return 조회된 Invitation
     * @throws InvitationException 초대를 찾을 수 없을 경우
     */
    public Invitation validateInvitationExists(UUID invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> {
                    log.warn("초대를 찾을 수 없음: invitationId={}", invitationId);
                    return new InvitationException(InvitationErrorCode.INVITATION_NOT_FOUND);
                });
    }

    /**
     * 토큰으로 초대 존재 여부 검증
     * - 토큰으로 초대 조회
     * - 존재하지 않으면 예외 발생
     *
     * @param token 초대 토큰
     * @return 조회된 Invitation
     * @throws InvitationException 유효하지 않은 토큰일 경우
     */
    public Invitation validateInvitationExistsByToken(String token) {
        return invitationRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("유효하지 않은 토큰: token={}", token);
                    return new InvitationException(InvitationErrorCode.INVALID_TOKEN);
                });
    }

    /**
     * 초대 유효성 검증 (상태 + 만료 여부)
     * - PENDING 상태인지 확인
     * - 만료되지 않았는지 확인
     *
     * @param invitation 검증할 초대
     * @throws InvitationException 이미 처리된 초대이거나 만료된 경우
     */
    public void validateInvitationValid(Invitation invitation) {
        // 이미 처리된 초대 (ACCEPTED, EXPIRED, CANCELED)
        if (invitation.getStatus().isCompleted()) {
            log.warn("이미 처리된 초대: invitationId={}, status={}",
                    invitation.getId(), invitation.getStatus());
            throw new InvitationException(InvitationErrorCode.INVITATION_ALREADY_PROCESSED);
        }

        // 만료된 초대
        if (invitation.isExpired()) {
            log.warn("만료된 초대: invitationId={}, expiresAt={}",
                    invitation.getId(), invitation.getExpiresAt());
            throw new InvitationException(InvitationErrorCode.INVITATION_EXPIRED);
        }
    }

    /**
     * 초대가 해당 업체에 속하는지 검증
     * - 재발송, 취소 시 권한 확인용
     *
     * @param invitation 검증할 초대
     * @param businessId 업체 ID
     * @throws InvitationException 초대가 해당 업체에 속하지 않을 경우
     */
    public void validateInvitationBelongsToBusiness(Invitation invitation, UUID businessId) {
        if (!invitation.getBusiness().getId().equals(businessId)) {
            log.warn("초대가 해당 업체에 속하지 않음: invitationId={}, businessId={}, actualBusinessId={}",
                    invitation.getId(), businessId, invitation.getBusiness().getId());
            throw new InvitationException(InvitationErrorCode.INVITATION_NOT_BELONG_TO_BUSINESS);
        }
    }

    /**
     * 사용자 이메일이 초대 이메일과 일치하는지 검증
     * - 초대 수락 시 다른 사람이 수락하지 못하게 방지
     *
     * @param userEmail 현재 사용자 이메일
     * @param invitation 검증할 초대
     * @throws InvitationException 이메일이 일치하지 않을 경우
     */
    public void validateUserEmailMatchesInvitation(String userEmail, Invitation invitation) {
        if (!userEmail.equalsIgnoreCase(invitation.getEmail())) {
            log.warn("이메일 불일치: userEmail={}, invitationEmail={}",
                    userEmail, invitation.getEmail());
            throw new InvitationException(InvitationErrorCode.EMAIL_MISMATCH);
        }
    }
}