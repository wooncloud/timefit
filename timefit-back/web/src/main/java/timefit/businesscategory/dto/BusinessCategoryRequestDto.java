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
                    example = "BD003",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {
                            "BD000 (음식점업)", "BD001 (숙박업)", "BD002 (소매/유통업)",
                            "BD003 (미용/뷰티업)", "BD004 (의료업)", "BD005 (피트니스/스포츠업)",
                            "BD006 (교육/문화업)", "BD007 (전문서비스업)", "BD008 (생활서비스업)",
                            "BD009 (제조/생산업)"
                    }
            )
            @NotNull(message = "업종은 필수입니다")
            BusinessTypeCode businessType,

            @Schema(
                    description = "카테고리명 (2~20자, 한글/영문/숫자/공백만 가능)",
                    example = "헤어 컷",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "카테고리명은 필수입니다")
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Schema(
                    description = "카테고리 공지사항 (최대 1000자)",
                    example = "예약 시 주의사항을 확인해주세요.",
                    nullable = true
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
                    description = "카테고리명 (2~20자, 한글/영문/숫자/공백만 가능)",
                    example = "헤어 펌",
                    nullable = true
            )
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Schema(
                    description = "카테고리 공지사항 (최대 1000자)",
                    example = "새로운 공지사항입니다.",
                    nullable = true
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