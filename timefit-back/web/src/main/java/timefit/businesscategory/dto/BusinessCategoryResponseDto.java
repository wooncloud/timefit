package timefit.businesscategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "업체 카테고리 응답")
public class BusinessCategoryResponseDto {

    /**
     * 카테고리 상세 정보
     * - 단일 카테고리 조회 시 사용
     * - 생성/수정 응답에도 사용
     */
    @Schema(description = "카테고리 상세")
    public record Category(
            @Schema(
                    description = "카테고리 ID",
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID categoryId,

            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업종 코드",
                    example = "BD003",
                    allowableValues = {
                            "BD000 (음식점업)", "BD001 (숙박업)", "BD002 (소매/유통업)",
                            "BD003 (미용/뷰티업)", "BD004 (의료업)", "BD005 (피트니스/스포츠업)",
                            "BD006 (교육/문화업)", "BD007 (전문서비스업)", "BD008 (생활서비스업)",
                            "BD009 (제조/생산업)"
                    }
            )
            BusinessTypeCode businessType,

            @Schema(
                    description = "카테고리명",
                    example = "헤어 컷"
            )
            String categoryName,

            @Schema(
                    description = "카테고리 공지사항",
                    example = "예약 시 주의사항을 확인해주세요.",
                    nullable = true
            )
            String categoryNotice,

            @Schema(
                    description = "활성화 상태 (true: 활성, false: 비활성)",
                    example = "true"
            )
            Boolean isActive,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최종 수정 일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt
    ) {
        // Entity → DTO 변환 정적 팩토리 메서드
        public static Category of(BusinessCategory category) {
            return new Category(
                    category.getId(),
                    category.getBusiness().getId(),
                    category.getBusinessType(),
                    category.getCategoryName(),
                    category.getCategoryNotice(),
                    category.getIsActive(),
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );
        }
    }

    // 카테고리 목록 응답
    @Schema(description = "카테고리 목록")
    public record CategoryList(
            @Schema(description = "카테고리 배열")
            List<Category> categories,

            @Schema(
                    description = "총 카테고리 개수",
                    example = "5"
            )
            Integer totalCount
    ) {
        // Entity List → DTO 변환 정적 팩토리 메서드
        public static CategoryList of(List<BusinessCategory> categoryEntities) {
            List<Category> categories = categoryEntities.stream()
                    .map(Category::of)
                    .toList();

            return new CategoryList(categories, categories.size());
        }
    }
}