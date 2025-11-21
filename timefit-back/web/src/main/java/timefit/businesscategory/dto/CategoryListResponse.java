package timefit.businesscategory.dto;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * 카테고리 목록 응답 DTO
 */
@Getter
public class CategoryListResponse {

    private final List<CategorySummary> categories;
    private final Integer totalCount;

    private CategoryListResponse(List<CategorySummary> categories, Integer totalCount) {
        this.categories = categories;
        this.totalCount = totalCount;
    }

    /**
     * 정적 팩토리 메서드
     *
     * @param categories 카테고리 요약 목록
     * @param totalCount 총 개수
     * @return CategoryListResponse
     */
    public static CategoryListResponse of(List<CategorySummary> categories, Integer totalCount) {
        return new CategoryListResponse(categories, totalCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryListResponse that = (CategoryListResponse) o;
        return Objects.equals(categories, that.categories) &&
                Objects.equals(totalCount, that.totalCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categories, totalCount);
    }
}