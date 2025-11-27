package timefit.invitation.dto;

import jakarta.validation.constraints.NotBlank;


public class InvitationRequestDto {

    // 초대 수락 요청
    public record AcceptInvitation(
            @NotBlank(message = "토큰은 필수입니다")
            String token
    ) {
    }
}