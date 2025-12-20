package timefit.invitation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.service.validator.BusinessValidator;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.invitation.entity.Invitation;
import timefit.invitation.repository.InvitationRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvitationQueryService {

    private final InvitationRepository invitationRepository;
    private final BusinessValidator businessValidator;

    /**
     * 업체별 초대 목록 조회
     * - 초대중인 사람 확인용
     *
     * @param businessId 업체 ID
     * @param currentUserId 현재 사용자 ID
     * @return 초대 목록
     */
    public InvitationResponseDto.InvitationList getInvitations(
            UUID businessId, UUID currentUserId) {

        // 권한 검증 (업체 구성원이면 조회 가능)
        businessValidator.validateBusinessAccess(currentUserId, businessId);
        log.info("초대 목록 조회: businessId={}", businessId);

        // 업체별 전체 초대 조회
        List<Invitation> invitations = invitationRepository.findByBusinessId(businessId);
        log.info("초대 목록 조회 완료: businessId={}, count={}", businessId, invitations.size());

        return InvitationResponseDto.InvitationList.from(invitations);
    }
}