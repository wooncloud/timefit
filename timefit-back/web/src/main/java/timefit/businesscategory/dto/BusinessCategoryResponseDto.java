package timefit.businesscategory.dto;

import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BusinessCategoryResponseDto {

    /**
     * 카테고리 상세 정보
     * - 단일 카테고리 조회 시 사용
     * - 생성/수정 응답에도 사용
     */
    public record Category(
            UUID categoryId,
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName,
            String categoryNotice,
            Boolean isActive,
            LocalDateTime createdAt,
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
    public record CategoryList(
            List<Category> categories,
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