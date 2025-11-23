package timefit.businesscategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import timefit.business.entity.BusinessTypeCode;

@Schema(description = "업체 카테고리 요청")
public class BusinessCategoryRequestDto {

    /**
     * 카테고리 생성 요청
     */
    @Schema(description = "카테고리 생성")
    public record CreateCategory(
            @Schema(
                    description = "업종 코드",
                    example = "BD008",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            @NotNull(message = "업종은 필수입니다")
            BusinessTypeCode businessType,

            @Schema(
                    description = "카테고리명 (2-20자, 한글/영문/숫자/공백만 가능)",
                    example = "헤어 컷",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minLength = 2,
                    maxLength = 20
            )
            @NotBlank(message = "카테고리명은 필수입니다")
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Schema(
                    description = "카테고리 공지사항",
                    example = "예약 시 주의사항을 확인해주세요.",
                    nullable = true,
                    maxLength = 1000
            )
            @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
            String categoryNotice
    ) {
    }

    /**
     * 카테고리 수정 요청
     * - 부분 수정 가능 (모든 필드 Optional)
     */
    @Schema(description = "카테고리 수정 (변경할 필드만 입력)")
    public record UpdateCategory(
            @Schema(
                    description = "카테고리명 (2-20자, 한글/영문/숫자/공백만 가능)",
                    example = "헤어 펌",
                    nullable = true,
                    minLength = 2,
                    maxLength = 20
            )
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Schema(
                    description = "카테고리 공지사항",
                    example = "새로운 공지사항입니다.",
                    nullable = true,
                    maxLength = 1000
            )
            @Size(max = 1000, message = "카테고리 공지사항은 1000자 이내여야 합니다")
            String categoryNotice,

            @Schema(
                    description = "활성화 상태 (true: 활성, false: 비활성)",
                    example = "true",
                    nullable = true
            )
            Boolean isActive
    ) {
    }
}