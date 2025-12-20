package timefit.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public class InvitationRequestDto {

    // 초대 수락 요청
    public record AcceptInvitation(
            @Schema(
                    description = "초대 토큰 (이메일 링크에 포함된 UUID)",
                    example = "141e0d7a-2149-4aa2-bfe1-619ca188d593",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "토큰은 필수입니다")
            String token
    ) {
    }
}