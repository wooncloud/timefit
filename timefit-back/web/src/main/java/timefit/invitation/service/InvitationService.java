package timefit.invitation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.entity.BusinessRole;
import timefit.invitation.dto.InvitationResponseDto;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationCommandService invitationCommandService;
    private final InvitationQueryService invitationQueryService;

    // 초대 발송
    public InvitationResponseDto.Invitation sendInvitation(
            UUID businessId,
            String email,
            BusinessRole role,
            UUID currentUserId) {

        log.info("초대 발송 요청: businessId={}, email={}, role={}", businessId, email, role);
        return invitationCommandService.sendInvitation(businessId, email, role, currentUserId);
    }

    // 초대 재발송
    public InvitationResponseDto.Invitation resendInvitation(
            UUID businessId,
            UUID invitationId,
            UUID currentUserId) {

        log.info("초대 재발송 요청: businessId={}, invitationId={}", businessId, invitationId);
        return invitationCommandService.resendInvitation(businessId, invitationId, currentUserId);
    }

    // 초대 수락
    public InvitationResponseDto.AcceptResult acceptInvitation(
            String token,
            UUID currentUserId) {

        log.info("초대 수락 요청: token={}, userId={}", token, currentUserId);
        return invitationCommandService.acceptInvitation(token, currentUserId);
    }

    // 초대 취소
    public void cancelInvitation(
            UUID businessId,
            UUID invitationId,
            UUID currentUserId) {

        log.info("초대 취소 요청: businessId={}, invitationId={}", businessId, invitationId);
        invitationCommandService.cancelInvitation(businessId, invitationId, currentUserId);
    }

    // 초대 목록 조회
    @Transactional(readOnly = true)
    public InvitationResponseDto.InvitationList getInvitations(
            UUID businessId,
            UUID currentUserId) {

        log.info("초대 목록 조회 요청: businessId={}", businessId);
        return invitationQueryService.getInvitations(businessId, currentUserId);
    }
}