package timefit.businesscategory.dto;

import lombok.Getter;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;

import java.util.Objects;
import java.util.UUID;

@Getter
public class CategorySummary {

    private final UUID categoryId;
    private final BusinessTypeCode businessType;

    /**
     * 카테고리명 (사용자 입력)
     * 예: "컷", "펌", "염색"
     */
    private final String categoryName;

    private final Boolean isActive;

    private CategorySummary(
            UUID categoryId,
            BusinessTypeCode businessType,
            String categoryName,
            Boolean isActive) {
        this.categoryId = categoryId;
        this.businessType = businessType;
        this.categoryName = categoryName;
        this.isActive = isActive;
    }

    /**
     * Entity → 목록용 DTO 변환 정적 팩토리 메서드
     *
     * @param category BusinessCategory Entity
     * @return CategorySummary DTO
     */
    public static CategorySummary of(BusinessCategory category) {
        return new CategorySummary(
                category.getId(),
                category.getBusinessType(),
                category.getCategoryName(),
                category.getIsActive()
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        CategorySummary that = (CategorySummary) other;
        return Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
}