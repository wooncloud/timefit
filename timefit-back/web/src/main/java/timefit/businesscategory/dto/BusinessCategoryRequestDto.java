package timefit.businesscategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import timefit.business.entity.BusinessTypeCode;

public class BusinessCategoryRequestDto {

    /**
     * 카테고리 생성 요청
     */
    public record CreateCategory(
            @NotNull(message = "업종은 필수입니다")
            BusinessTypeCode businessType,

            @NotBlank(message = "카테고리명은 필수입니다")
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
            String categoryNotice
    ) {
    }

    /**
     * 카테고리 수정 요청
     * - 부분 수정 가능 (모든 필드 Optional)
     */
    public record UpdateCategory(
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
            String categoryNotice,

            Boolean isActive
    ) {
    }
}