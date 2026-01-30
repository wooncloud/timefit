package timefit.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.Business;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User 응답 DTO
 */
@Schema(description = "사용자 응답")
public class UserResponseDto {

    /**
     * 현재 로그인한 사용자의 전체 정보
     * - 사업자: businesses 배열 포함
     * - 일반 고객: businesses 빈 배열
     *
     * 사용처: /api/user/me (사업자 + 고객 공용)
     */
    @Schema(description = "현재 사용자 정보")
    public record CurrentUser(
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

            @Schema(description = "소속 업체 목록 (사업자인 경우)")
            List<BusinessInfo> businesses
    ) {
        /**
         * User 엔티티 + 업체 목록으로 DTO 생성
         */
        public static CurrentUser of(User user, List<BusinessInfo> businesses) {
            return new CurrentUser(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getProfileImageUrl(),
                    user.getCreatedAt(),
                    user.getLastLoginAt(),
                    businesses != null ? businesses : List.of()
            );
        }
    }

    /**
     * 업체 정보 (네비게이션바용)
     */
    @Schema(description = "업체 정보")
    public record BusinessInfo(
            @Schema(description = "업체 ID", example = "30000000-0000-0000-0000-000000000001")
            UUID businessId,

            @Schema(description = "업체명", example = "스타일 헤어샵")
            String businessName,

            @Schema(description = "로고 URL", example = "https://example.com/logo.png")
            String logoUrl,

            @Schema(description = "내 역할", example = "OWNER")
            String myRole,

            @Schema(description = "활성 상태", example = "true")
            Boolean isActive
    ) {
        /**
         * Business 엔티티 + 역할로 DTO 생성
         */
        public static BusinessInfo from(Business business, String role) {
            return new BusinessInfo(
                    business.getId(),
                    business.getBusinessName(),
                    business.getLogoUrl(),
                    role,
                    business.getIsActive()
            );
        }
    }

    /**
     * 사용자 프로필 (통계 포함)
     *
     * 사용처: /api/customer/profile (고객 전용)
     */
    @Schema(description = "사용자 프로필")
    public record UserProfile(
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
         * User 엔티티와 통계로 UserProfile 생성
         */
        public static UserProfile of(User user, UserStatistics statistics) {
            return new UserProfile(
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