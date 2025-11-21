package timefit.businesscategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.business.entity.BusinessTypeCode;

public class BusinessCategoryRequest {

    /**
     * 카테고리 생성 요청
     * 사용처: POST /api/business/{businessId}/categories
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CreateCategory {

        @NotNull(message = "업종은 필수입니다")
        private BusinessTypeCode businessType;

        @NotBlank(message = "카테고리명은 필수입니다")
        @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
        private String categoryName;

        @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
        private String categoryNotice;

        // 테스트 및 수동 생성용 생성자
        public CreateCategory(
                BusinessTypeCode businessType,
                String categoryName,
                String categoryNotice) {
            this.businessType = businessType;
            this.categoryName = categoryName;
            this.categoryNotice = categoryNotice;
        }
    }

    /**
     * 카테고리 수정 요청
     * 사용처: PATCH /api/business/{businessId}/categories/{categoryId}
     * - 부분 수정 가능 (모든 필드 Optional)
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateCategory {

        @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
        private String categoryName;

        @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
        private String categoryNotice;

        private Boolean isActive;

        // 테스트 및 수동 생성용 생성자
        public UpdateCategory(
                String categoryName,
                String categoryNotice,
                Boolean isActive) {
            this.categoryName = categoryName;
            this.categoryNotice = categoryNotice;
            this.isActive = isActive;
        }
    }
}