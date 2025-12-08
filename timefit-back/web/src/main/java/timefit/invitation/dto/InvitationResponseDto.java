package timefit.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "초대 정보")
    public record Invitation(
            @Schema(description = "초대 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID invitationId,

            @Schema(description = "초대받은 이메일", example = "user@example.com")
            String email,

            @Schema(description = "초대 역할 (OWNER/MANAGER/STAFF)", example = "MANAGER")
            BusinessRole role,

            @Schema(description = "초대 상태 (PENDING/ACCEPTED/EXPIRED/CANCELED)", example = "PENDING")
            InvitationStatus status,

            @Schema(description = "초대한 사람 이름", example = "홍길동")
            String invitedByName,

            @Schema(description = "초대 만료 시간", example = "2025-12-15T23:59:59")
            LocalDateTime expiresAt,

            @Schema(description = "초대 생성 시간", example = "2025-12-08T10:00:00")
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
    @Schema(description = "초대 목록")
    public record InvitationList(
            @Schema(description = "초대 목록")
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
    @Schema(description = "초대 수락 결과")
    public record AcceptResult(
            @Schema(description = "업체 ID", example = "550e8400-e29b-41d4-a716-446655440001")
            UUID businessId,

            @Schema(description = "업체명", example = "강남 헤어샵")
            String businessName,

            @Schema(description = "부여된 역할 (OWNER/MANAGER/STAFF)", example = "MANAGER")
            BusinessRole role,

            @Schema(description = "결과 메시지", example = "강남 헤어샵의 MANAGER로 추가되었습니다")
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