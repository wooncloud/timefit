package timefit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * User 요청 DTO (고객용)
 */
@Schema(description = "사용자 요청")
public class UserRequestDto {

    /**
     * 프로필 수정 요청 (모두 선택)
     */
    @Schema(description = "프로필 수정 요청")
    public record UpdateProfile(
            @Schema(
                    description = "이름",
                    example = "홍길동",
                    minLength = 2,
                    maxLength = 50
            )
            @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
            String name,

            @Schema(
                    description = "전화번호",
                    example = "010-1234-5678",
                    pattern = "^01[0-9]-\\d{3,4}-\\d{4}$"
            )
            @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다")
            String phoneNumber,

            @Schema(
                    description = "프로필 이미지 URL",
                    example = "https://example.com/profile.jpg"
            )
            String profileImageUrl
    ) {
    }

    /**
     * 비밀번호 변경 요청
     */
    @Schema(description = "비밀번호 변경 요청")
    public record ChangePassword(
            @Schema(
                    description = "현재 비밀번호",
                    example = "oldPassword123!",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "현재 비밀번호는 필수입니다")
            String currentPassword,

            @Schema(
                    description = "새 비밀번호",
                    example = "newPassword123!",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minLength = 8,
                    maxLength = 20,
                    pattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$"
            )
            @NotBlank(message = "새 비밀번호는 필수입니다")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다"
            )
            String newPassword,

            @Schema(
                    description = "새 비밀번호 확인",
                    example = "newPassword123!",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "새 비밀번호 확인은 필수입니다")
            String newPasswordConfirm
    ) {
    }
}