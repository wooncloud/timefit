package timefit.invitation.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.invitation.*;
import timefit.common.swagger.requestbody.invitation.*;
import timefit.invitation.dto.InvitationRequestDto;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.invitation.service.InvitationService;

import java.util.UUID;

@Tag(name = "08. 초대 관리", description = "업체 구성원 초대 관리 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * 초대 목록 조회
     */
    @GetInvitationsOperation
    @GetMapping("/business/{businessId}/invitations")
    public ResponseEntity<ResponseData<InvitationResponseDto.InvitationList>> getInvitations(
            @PathVariable UUID businessId,
            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("초대 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        InvitationResponseDto.InvitationList invitations =
                invitationService.getInvitations(businessId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(invitations));
    }

    /**
     * 초대 재발송
     */
    @ResendInvitationOperation
    @PostMapping("/business/{businessId}/invitation/{invitationId}/resend")
    public ResponseEntity<ResponseData<InvitationResponseDto.Invitation>> resendInvitation(
            @PathVariable UUID businessId,
            @PathVariable UUID invitationId,
            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("초대 재발송 요청: businessId={}, invitationId={}, userId={}",
                businessId, invitationId, currentUserId);

        InvitationResponseDto.Invitation invitation =
                invitationService.resendInvitation(businessId, invitationId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(invitation));
    }

    /**
     * 초대 수락
     */
    @AcceptInvitationOperation
    @PostMapping("/invitation/accept")
    public ResponseEntity<ResponseData<InvitationResponseDto.AcceptResult>> acceptInvitation(
            @AcceptInvitationRequestBody @Valid @RequestBody InvitationRequestDto.AcceptInvitation request,
            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("초대 수락 요청: token={}, userId={}", request.token(), currentUserId);

        InvitationResponseDto.AcceptResult result =
                invitationService.acceptInvitation(request.token(), currentUserId);

        return ResponseEntity.ok(ResponseData.of(result));
    }

    /**
     * 초대 취소
     */
    @CancelInvitationOperation
    @DeleteMapping("/business/{businessId}/invitation/{invitationId}")
    public ResponseEntity<ResponseData<Void>> cancelInvitation(
            @PathVariable UUID businessId,
            @PathVariable UUID invitationId,
            @Parameter(hidden = true) @CurrentUserId UUID currentUserId) {

        log.info("초대 취소 요청: businessId={}, invitationId={}, userId={}",
                businessId, invitationId, currentUserId);

        invitationService.cancelInvitation(businessId, invitationId, currentUserId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}