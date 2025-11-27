package timefit.invitation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.common.email.EmailService;
import timefit.common.entity.BusinessRole;
import timefit.exception.system.SystemErrorCode;
import timefit.exception.system.SystemException;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.invitation.entity.Invitation;
import timefit.invitation.entity.InvitationStatus;
import timefit.invitation.repository.InvitationRepository;
import timefit.invitation.service.validator.InvitationValidator;
import timefit.user.entity.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InvitationCommandService {

    private final InvitationRepository invitationRepository;
    private final InvitationValidator invitationValidator;
    private final BusinessValidator businessValidator;
    private final AuthValidator authValidator;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final EmailService emailService;

    //    [설정 항목]
    //    1. base-url: 프론트엔드 서버의 기본 URL
    //    2. invite-path: 초대 수락 페이지의 라우팅 경로
    @Value("${timefit.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${timefit.frontend.invite-path}")
    private String frontendInvitePath;

    /**
     * 초대 발송
     * - PENDING 초대가 이미 있으면 기존 것 반환 (중복 방지)
     * - 없으면 새로 생성하여 이메일 발송
     *
     * @param businessId 업체 ID
     * @param email 초대할 이메일
     * @param role 초대 역할
     * @param currentUserId 현재 사용자 ID
     * @return 초대 정보
     */
    public InvitationResponseDto.Invitation sendInvitation(
            UUID businessId,
            String email,
            BusinessRole role,
            UUID currentUserId) {

        // 1. 권한 검증 (OWNER/MANAGER만 가능)
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);
        User invitedBy = authValidator.validateUserExists(currentUserId);
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 이미 PENDING 초대가 있는지 확인
        Optional<Invitation> existing = invitationRepository.findByBusinessIdAndEmailAndStatus(
                businessId,
                email.toLowerCase(),
                InvitationStatus.PENDING
        );

        if (existing.isPresent()) {
            log.info("이미 PENDING 초대 존재 - 기존 것 반환: invitationId={}, email={}",
                    existing.get().getId(), email);
            return InvitationResponseDto.Invitation.from(existing.get());
        }

        // 3. 중복 검증 (이미 구성원인지)
        invitationValidator.validateNotAlreadyMember(businessId, email);

        // 4. 초대 생성
        Invitation invitation = Invitation.create(business, email, invitedBy, role);
        Invitation saved = invitationRepository.save(invitation);

        log.info("초대 생성 완료: invitationId={}, businessId={}, email={}, role={}",
                saved.getId(), businessId, email, role);

        // 5. 이메일 발송 (비동기)
        sendInvitationEmail(saved);

        return InvitationResponseDto.Invitation.from(saved);
    }

    /**
     * 초대 재발송
     * - 만료 시간 연장
     * - 이메일 재발송
     *
     * @param businessId 업체 ID
     * @param invitationId 초대 ID
     * @param currentUserId 현재 사용자 ID
     * @return 초대 정보
     */
    public InvitationResponseDto.Invitation resendInvitation(
            UUID businessId,
            UUID invitationId,
            UUID currentUserId) {

        // 1. 권한 검증 (OWNER/MANAGER만 가능)
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 초대 검증
        Invitation invitation = invitationValidator.validateInvitationExists(invitationId);
        invitationValidator.validateInvitationBelongsToBusiness(invitation, businessId);
        invitationValidator.validateInvitationValid(invitation);

        log.info("초대 재발송 시작: invitationId={}, businessId={}", invitationId, businessId);

        // 3. 만료 시간 연장
        invitation.extendExpiration();

        log.info("초대 만료 시간 연장: invitationId={}, newExpiresAt={}",
                invitationId, invitation.getExpiresAt());

        // 4. 이메일 재발송 (비동기)
        sendInvitationEmail(invitation);

        return InvitationResponseDto.Invitation.from(invitation);
    }

    /**
     * 초대 수락
     * - 이메일 일치 확인
     * - UserBusinessRole 생성
     *
     * @param token 초대 토큰
     * @param currentUserId 현재 사용자 ID
     * @return 수락 결과
     */
    public InvitationResponseDto.AcceptResult acceptInvitation(
            String token,
            UUID currentUserId) {

        // 1. 초대 조회 및 검증
        Invitation invitation = invitationValidator.validateInvitationExistsByToken(token);
        invitationValidator.validateInvitationValid(invitation);

        // 2. 사용자 조회 및 이메일 일치 검증
        User user = authValidator.validateUserExists(currentUserId);
        invitationValidator.validateUserEmailMatchesInvitation(user.getEmail(), invitation);

        log.info("초대 수락 시작: invitationId={}, userId={}, email={}",
                invitation.getId(), currentUserId, user.getEmail());

        // 3. 초대 수락 처리
        invitation.accept();

        // 4. UserBusinessRole 생성
        UserBusinessRole userBusinessRole = UserBusinessRole.createWithRole(
                user,
                invitation.getBusiness(),
                invitation.getRole(),
                invitation.getInvitedBy()
        );
        userBusinessRoleRepository.save(userBusinessRole);

        log.info("구성원 추가 완료: userId={}, businessId={}, role={}",
                currentUserId, invitation.getBusiness().getId(), invitation.getRole());

        return InvitationResponseDto.AcceptResult.of(
                invitation.getBusiness().getId(),
                invitation.getBusiness().getBusinessName(),
                invitation.getRole()
        );
    }

    /**
     * 초대 취소
     * - PENDING 상태만 취소 가능
     *
     * @param businessId 업체 ID
     * @param invitationId 초대 ID
     * @param currentUserId 현재 사용자 ID
     */
    public void cancelInvitation(
            UUID businessId,
            UUID invitationId,
            UUID currentUserId) {

        // 1. 권한 검증 (OWNER/MANAGER만 가능)
        businessValidator.validateManagerOrOwnerRole(currentUserId, businessId);

        // 2. 초대 검증
        Invitation invitation = invitationValidator.validateInvitationExists(invitationId);
        invitationValidator.validateInvitationBelongsToBusiness(invitation, businessId);
        invitationValidator.validateInvitationValid(invitation);

        log.info("초대 취소: invitationId={}, businessId={}", invitationId, businessId);

        // 3. 초대 취소 처리
        invitation.cancel();

        log.info("초대 취소 완료: invitationId={}", invitationId);
    }

    /**
     * 초대 이메일 발송 (비동기)
     */
    private void sendInvitationEmail(Invitation invitation) {
        try {
            String inviteLink = generateInviteLink(invitation.getToken());
            String subject = String.format("[Timefit] %s 초대장",
                    invitation.getBusiness().getBusinessName());

            // HTML 템플릿 파일에서 콘텐츠 생성
            String htmlContent = loadEmailTemplate(
                    invitation.getBusiness().getBusinessName(),
                    invitation.getInvitedBy().getName(),
                    invitation.getRole().name(),
                    inviteLink,
                    invitation.getExpiresAt().toString()
            );

            emailService.sendHtmlEmailAsync(
                    invitation.getEmail(),
                    subject,
                    htmlContent
            );

            log.info("초대 이메일 발송 요청: email={}, token={}",
                    invitation.getEmail(), invitation.getToken());

        } catch (Exception e) {
            log.error("초대 이메일 발송 실패: invitationId={}, email={}",
                    invitation.getId(), invitation.getEmail(), e);
            // 이메일 발송 실패해도 초대는 생성됨 (재발송 가능)
        }
    }

    /**
     * 초대 링크 생성
     * 예: http://localhost:3000/invite/141e0d7a-2149-4aa2-bfe1-619ca188d593
     */
    private String generateInviteLink(String token) {
        return frontendBaseUrl + frontendInvitePath + "/" + token;
    }

    /**
     * HTML template file load 및 placeholder 치환
     */
    private String loadEmailTemplate(
            String businessName,
            String inviterName,
            String roleName,
            String inviteLink,
            String expiresAt) {

        try {
            // resources/public/email/invitation.html 읽기
            ClassPathResource resource = new ClassPathResource("public/email/invitation.html");
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String template = FileCopyUtils.copyToString(reader);

            // placeholder 치환
            return template
                    .replace("{{BUSINESS_NAME}}", businessName)
                    .replace("{{INVITER_NAME}}", inviterName)
                    .replace("{{ROLE_NAME}}", roleName)
                    .replace("{{INVITE_LINK}}", inviteLink)
                    .replace("{{EXPIRES_AT}}", expiresAt);

        } catch (IOException e) {
            log.error("이메일 템플릿 로드 실패: {}", e.getMessage(), e);
            throw new SystemException(
                    SystemErrorCode.FILE_STORAGE_ERROR,
                    "이메일 템플릿 파일을 찾을 수 없습니다"
            );
        }
    }
}