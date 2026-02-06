package timefit.wishlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.BusinessTypeCode;
import timefit.wishlist.entity.Wishlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Wishlist 응답 DTO
 */
@Schema(description = "찜 관리 응답")
public class WishlistResponseDto {

    /**
     * 찜 목록 아이템
     */
    @Schema(description = "찜 아이템")
    public record WishlistItem(
            @Schema(description = "찜 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID wishlistId,

            @Schema(description = "업체명", example = "Owner Kim 미용실")
            String businessName,

            @Schema(description = "업종 코드 목록", example = "[\"BD008\"]")
            Set<BusinessTypeCode> businessTypes,

            @Schema(description = "대표자명", example = "Owner Kim")
            String ownerName,

            @Schema(description = "업체 주소", example = "서울특별시 강남구 테헤란로 123")
            String address,

            @Schema(description = "업체 연락처", example = "02-1111-1111")
            String contactPhone,

            @Schema(description = "업체 설명", example = "20년 경력의 전문 미용실입니다.")
            String description,

            @Schema(description = "로고 이미지 URL", example = "https://example.com/logo.png")
            String logoUrl,

            @Schema(description = "평균 평점 (0.0~5.0)", example = "4.5")
            Double averageRating,

            @Schema(description = "리뷰 개수", example = "120")
            Integer reviewCount,

            @Schema(description = "위도 (위치 기반 검색용)", example = "37.5665")
            Double latitude,

            @Schema(description = "경도 (위치 기반 검색용)", example = "126.9780")
            Double longitude,

            @Schema(description = "찜한 날짜", example = "2026-01-30T10:00:00")
            LocalDateTime createdAt
    ) {
        /**
         * Wishlist 엔티티를 WishlistItem으로 변환
         */
        public static WishlistItem from(Wishlist wishlist) {
            return new WishlistItem(
                    wishlist.getId(),
                    wishlist.getBusiness().getBusinessName(),
                    wishlist.getBusiness().getBusinessTypes(),
                    wishlist.getBusiness().getOwnerName(),
                    wishlist.getBusiness().getAddress(),
                    wishlist.getBusiness().getContactPhone(),
                    wishlist.getBusiness().getDescription(),
                    wishlist.getBusiness().getLogoUrl(),
                    wishlist.getBusiness().getAverageRating(),
                    wishlist.getBusiness().getReviewCount(),
                    wishlist.getBusiness().getLatitude(),
                    wishlist.getBusiness().getLongitude(),
                    wishlist.getCreatedAt()
            );
        }
    }

    /**
     * 찜 목록 응답 (페이징)
     */
    @Schema(description = "찜 목록 응답")
    public record WishlistList(
            @Schema(description = "찜 목록")
            List<WishlistItem> wishlists,

            @Schema(description = "전체 개수", example = "15")
            long totalCount,

            @Schema(description = "현재 페이지 (0부터 시작)", example = "0")
            int page,

            @Schema(description = "페이지 크기", example = "20")
            int size,

            @Schema(description = "전체 페이지 수", example = "1")
            int totalPages
    ) {
        /**
         * 찜 목록과 페이지 정보로 응답 생성
         */
        public static WishlistList of(
                List<WishlistItem> wishlists,
                long totalCount,
                int page,
                int size,
                int totalPages) {
            return new WishlistList(wishlists, totalCount, page, size, totalPages);
        }
    }

    /**
     * 찜 추가/삭제 결과
     */
    @Schema(description = "찜 액션 결과")
    public record WishlistAction(
            @Schema(description = "성공 여부", example = "true")
            boolean success,

            @Schema(description = "메시지", example = "찜 목록에 추가되었습니다")
            String message,

            @Schema(description = "메뉴 ID", example = "10000000-0000-0000-0000-000000000001")
            UUID businessId
    ) {
        /**
         * 찜 추가 성공 응답
         */
        public static WishlistAction addSuccess(UUID businessId) {
            return new WishlistAction(true, "찜 목록에 추가되었습니다", businessId);
        }

        /**
         * 찜 삭제 성공 응답
         */
        public static WishlistAction removeSuccess(UUID businessId) {
            return new WishlistAction(true, "찜 목록에서 제거되었습니다", businessId);
        }
    }
}