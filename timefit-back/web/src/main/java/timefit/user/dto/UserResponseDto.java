package timefit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User 응답 DTO (고객용)
 */
@Schema(description = "사용자 응답")
public class UserResponseDto {

    /**
     * 사용자 프로필 (통계 포함)
     */
    @Schema(description = "사용자 프로필")
    public record UserProfileResponse(
            @Schema(description = "사용자 ID", example = "20000000-0000-0000-0000-000000000001")
            UUID userId,

            @Schema(description = "이메일", example = "user@example.com")
            String email,

            @Schema(description = "이름", example = "홍길동")
            String name,

            @Schema(description = "전화번호", example = "010-1234-5678")
            String phoneNumber,

            @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
            String profileImageUrl,

            @Schema(description = "가입일시", example = "2026-01-01T10:00:00")
            LocalDateTime createdAt,

            @Schema(description = "마지막 로그인", example = "2026-01-30T09:00:00")
            LocalDateTime lastLoginAt,

            @Schema(description = "통계 정보")
            UserStatistics statistics
    ) {
        /**
         * User 엔티티와 통계로 UserProfileResponse 생성
         */
        public static UserProfileResponse of(User user, UserStatistics statistics) {
            return new UserProfileResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getProfileImageUrl(),
                    user.getCreatedAt(),
                    user.getLastLoginAt(),
                    statistics
            );
        }
    }

    /**
     * 사용자 통계
     */
    @Schema(description = "사용자 통계")
    public record UserStatistics(
            @Schema(description = "전체 예약 수", example = "15")
            long totalReservations,

            @Schema(description = "찜 개수", example = "8")
            long wishlistCount,

            @Schema(description = "작성한 리뷰 수", example = "5")
            long reviewCount
    ) {
        /**
         * 통계 데이터로 UserStatistics 생성
         */
        public static UserStatistics of(
                long totalReservations,
                long wishlistCount,
                long reviewCount) {
            return new UserStatistics(totalReservations, wishlistCount, reviewCount);
        }

        /**
         * 빈 통계 (기본값 0)
         */
        public static UserStatistics empty() {
            return new UserStatistics(0L, 0L, 0L);
        }
    }
}