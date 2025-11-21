package timefit.businesscategory.dto;

import lombok.Getter;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BusinessCategoryResponse {

    private final UUID categoryId;
    private final UUID businessId;
    private final BusinessTypeCode businessType;

    /**
     * 카테고리명 (사용자 입력)
     * 예: "컷", "펌", "염색"
     */
    private final String categoryName;

    private final String categoryNotice;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private BusinessCategoryResponse(
            UUID categoryId,
            UUID businessId,
            BusinessTypeCode businessType,
            String categoryName,
            String categoryNotice,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.categoryId = categoryId;
        this.businessId = businessId;
        this.businessType = businessType;
        this.categoryName = categoryName;
        this.categoryNotice = categoryNotice;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Entity → DTO 변환 정적 팩토리 메서드
     *
     * @param category BusinessCategory Entity
     * @return BusinessCategoryResponse DTO
     */
    public static BusinessCategoryResponse of(BusinessCategory category) {
        return new BusinessCategoryResponse(
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        BusinessCategoryResponse that = (BusinessCategoryResponse) other;
        return Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
}