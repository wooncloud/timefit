package timefit.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.OrderType;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "메뉴 요청")
public class MenuRequestDto {

    /**
     * 메뉴 생성/수정 통합 요청
     * - 생성: 모든 필드 필수
     * - 수정: null이 아닌 필드만 업데이트
     * NOTE: 검증 규칙
     * - RESERVATION_BASED일 때 durationMinutes 필수 (Validator 에서 검증)
     * - autoGenerateSlots=true일 때 slotSettings 필수 (Validator 에서 검증)
     */
    @Schema(description = "메뉴 생성/수정 (생성 시 필수값, 수정 시 변경할 필드만 입력)")
    public record CreateUpdateMenu(
            @Schema(
                    description = "업종 코드",
                    example = "BD008",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            @NotNull(message = "업종은 필수입니다")
            BusinessTypeCode businessType,

            @Schema(
                    description = "카테고리명 (2~20자, 한글/영문/숫자/공백만 가능)",
                    example = "헤어",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "카테고리명은 필수입니다")
            @Size(min = 2, max = 20, message = "카테고리명은 2~20자여야 합니다")
            String categoryName,

            @Schema(
                    description = "서비스명 (최대 100자)",
                    example = "헤어 컷",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "서비스명은 필수입니다")
            @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
            String serviceName,

            @Schema(
                    description = "가격 (0보다 커야 함)",
                    example = "30000",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "가격은 필수입니다")
            @Positive(message = "가격은 0보다 커야 합니다")
            Integer price,

            @Schema(
                    description = "서비스 설명 (최대 500자)",
                    example = "기본 헤어 컷 서비스입니다",
                    nullable = true
            )
            @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
            String description,

            @Schema(
                    description = "이미지 URL (최대 200자)",
                    example = "https://example.com/image.jpg",
                    nullable = true
            )
            @Size(max = 200, message = "이미지 URL은 200자 이하로 입력해주세요")
            String imageUrl,

            @Schema(
                    description = "서비스 유형",
                    example = "RESERVATION_BASED",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"RESERVATION_BASED", "ONDEMAND_BASED"}
            )
            @NotNull(message = "서비스 유형은 필수입니다")
            OrderType orderType,

            /**
             * 소요 시간 (분)
             * - RESERVATION_BASED일 때 필수 (Validator 에서 검증)
             * - ONDEMAND_BASED일 때 선택
             */
            @Schema(
                    description = "소요 시간 (분 단위, 1~1440분). RESERVATION_BASED일 때 필수",
                    example = "60",
                    nullable = true
            )
            @Positive(message = "소요 시간은 0보다 커야 합니다")
            @Max(value = 1440, message = "소요 시간은 1440분(24시간) 이하여야 합니다")
            Integer durationMinutes,

            // BookingSlot 자동 생성
            /**
             * BookingSlot 자동 생성 여부
             * - true: slotSettings 필수
             * - false 또는 null: 슬롯 생성 안 함
             */
            @Schema(
                    description = "BookingSlot 자동 생성 여부 (true일 때 slotSettings 필수)",
                    example = "true",
                    nullable = true
            )
            Boolean autoGenerateSlots,

            /**
             * BookingSlot 생성 설정
             * - autoGenerateSlots=true일 때 필수
             */
            @Schema(
                    description = "BookingSlot 생성 설정 (autoGenerateSlots=true일 때 필수)",
                    nullable = true
            )
            BookingSlotSettings slotSettings
    ) {}

    @Schema(description = "BookingSlot 생성 설정")
    public record BookingSlotSettings(
            @Schema(
                    description = "슬롯 시작 날짜",
                    example = "2025-12-01",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "슬롯 시작 날짜는 필수입니다")
            LocalDate startDate,

            @Schema(
                    description = "슬롯 종료 날짜",
                    example = "2025-12-31",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "슬롯 종료 날짜는 필수입니다")
            LocalDate endDate,

            @Schema(
                    description = "슬롯 간격 (분 단위, 15~480분)",
                    example = "30",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "슬롯 간격은 필수입니다")
            @Min(value = 15, message = "슬롯 간격은 최소 15분입니다")
            @Max(value = 480, message = "슬롯 간격은 최대 480분(8시간)입니다")
            Integer slotIntervalMinutes,

            /**
             * 특정 시간대만 생성 (선택)
             * - null 이면 영업시간 전체에 슬롯 생성
             * - 지정하면 해당 시간대에만 슬롯 생성
             */
            @Schema(
                    description = "특정 시간대만 생성 (null이면 영업시간 전체에 슬롯 생성)",
                    nullable = true
            )
            List<@Valid TimeRange> specificTimeRanges
    ) {}

    /**
     * 시간대 설정
     * NOTE: 시간 형식 검증은 Controller의 @Valid 또는 Validator 에서 처리
     * - 형식: "HH:mm" (예: "09:00", "18:30")
     * - startTime < endTime
     */
    @Schema(description = "시간대 설정")
    public record TimeRange(
            @Schema(
                    description = "시작 시간 (HH:mm 형식)",
                    example = "09:00",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "시작 시간은 필수입니다")
            String startTime,  // "09:00" 형식

            @Schema(
                    description = "종료 시간 (HH:mm 형식)",
                    example = "18:00",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotNull(message = "종료 시간은 필수입니다")
            String endTime     // "18:00" 형식
    ) {}
}