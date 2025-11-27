package timefit.invitation.dto;

import timefit.common.entity.BusinessRole;
import timefit.invitation.entity.InvitationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InvitationResponseDto {

    /**
     * 초대 정보 응답
     * - 초대 발송, 재발송 시 사용
     */
    public record Invitation(
            UUID invitationId,
            String email,
            BusinessRole role,
            InvitationStatus status,
            String invitedByName,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        public static Invitation from(timefit.invitation.entity.Invitation invitation) {
            return new Invitation(
                    invitation.getId(),
                    invitation.getEmail(),
                    invitation.getRole(),
                    invitation.getStatus(),
                    invitation.getInvitedBy().getName(),
                    invitation.getExpiresAt(),
                    invitation.getCreatedAt()
            );
        }
    }

    /**
     * 초대 목록 응답
     * - 업체별 초대 목록 조회 시 사용
     */
    public record InvitationList(
            List<Invitation> invitations
    ) {
        public static InvitationList from(List<timefit.invitation.entity.Invitation> invitations) {
            return new InvitationList(
                    invitations.stream()
                            .map(Invitation::from)
                            .toList()
            );
        }
    }

    /**
     * 초대 수락 결과 응답
     * - 초대 수락 시 사용
     */
    public record AcceptResult(
            UUID businessId,
            String businessName,
            BusinessRole role,
            String message
    ) {
        public static AcceptResult of(UUID businessId, String businessName, BusinessRole role) {
            return new AcceptResult(
                    businessId,
                    businessName,
                    role,
                    String.format("%s의 %s로 추가되었습니다", businessName, role.name())
            );
        }
    }
}