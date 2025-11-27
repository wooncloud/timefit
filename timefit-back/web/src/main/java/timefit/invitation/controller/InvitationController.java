package timefit.invitation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.invitation.dto.InvitationRequestDto;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.invitation.service.InvitationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * 초대 목록 조회
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 초대 목록
     */
    @GetMapping("/business/{businessId}/invitations")
    public ResponseEntity<ResponseData<InvitationResponseDto.InvitationList>> getInvitations(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("초대 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        InvitationResponseDto.InvitationList invitations =
                invitationService.getInvitations(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(invitations));
    }

    /**
     * 초대 재발송
     * @param businessId 업체 ID
     * @param invitationId 초대 ID
     * @param currentUserId 현재 사용자 ID
     * @return 초대 정보
     */
    @PostMapping("/business/{businessId}/invitation/{invitationId}/resend")
    public ResponseEntity<ResponseData<InvitationResponseDto.Invitation>> resendInvitation(
            @PathVariable UUID businessId,
            @PathVariable UUID invitationId,
            @CurrentUserId UUID currentUserId) {

        log.info("초대 재발송 요청: businessId={}, invitationId={}, userId={}",
                businessId, invitationId, currentUserId);

        InvitationResponseDto.Invitation invitation =
                invitationService.resendInvitation(businessId, invitationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(invitation));
    }

    /**
     * 초대 수락
     * @param request 수락 요청 (token)
     * @param currentUserId 현재 사용자 ID
     * @return 수락 결과
     */
    @PostMapping("/invitation/accept")
    public ResponseEntity<ResponseData<InvitationResponseDto.AcceptResult>> acceptInvitation(
            @Valid @RequestBody InvitationRequestDto.AcceptInvitation request,
            @CurrentUserId UUID currentUserId) {

        log.info("초대 수락 요청: token={}, userId={}", request.token(), currentUserId);

        InvitationResponseDto.AcceptResult result =
                invitationService.acceptInvitation(request.token(), currentUserId);

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * 초대 취소
     * @param businessId 업체 ID
     * @param invitationId 초대 ID
     * @param currentUserId 현재 사용자 ID
     * @return 204 No Content
     */
    @DeleteMapping("/business/{businessId}/invitation/{invitationId}")
    public ResponseEntity<ResponseData<Void>> cancelInvitation(
            @PathVariable UUID businessId,
            @PathVariable UUID invitationId,
            @CurrentUserId UUID currentUserId) {

        log.info("초대 취소 요청: businessId={}, invitationId={}, userId={}",
                businessId, invitationId, currentUserId);

        invitationService.cancelInvitation(businessId, invitationId, currentUserId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}