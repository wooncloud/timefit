package timefit.operatinghours.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "영업시간 관리 Request DTO 모음")
public class OperatingHoursRequestDto {

    // 영업시간 + 예약 가능 시간대 통합 설정
    @Schema(description = "영업시간 설정 요청")
    public record SetOperatingHours(
            @NotNull(message = "스케줄 목록은 필수입니다")
            @Schema(description = "요일별 스케줄 목록 (7개 요일 전체)",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            List<DaySchedule> schedules
    ) {}

    // 요일별 스케줄
    @Schema(description = "요일별 영업시간 및 예약 가능 시간대 설정")
    public record DaySchedule(
            @NotNull(message = "요일은 필수입니다")
            @Min(value = 0, message = "요일은 0(일요일)부터 6(토요일)까지입니다")
            @Max(value = 6, message = "요일은 0(일요일)부터 6(토요일)까지입니다")
            @Schema(description = "요일 (0=일요일, 1=월요일, ..., 6=토요일)",
                    example = "1",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minimum = "0",
                    maximum = "6")
            Integer dayOfWeek,

            // 이 영업시간 (BusinessHours)
            @Schema(description = "영업 시작 시간 (HH:mm 형식)", example = "09:00", nullable = true)
            String openTime,        // "09:00"

            @Schema(description = "영업 종료 시간 (HH:mm 형식)", example = "22:00", nullable = true)
            String closeTime,       // "22:00"

            @Schema(description = "휴무일 여부", example = "false", nullable = true)
            Boolean isClosed,       // 휴무일 여부

            // 예약 가능 시간대 (OperatingHours)
            @Schema(description = "예약 가능 시간대 목록 (영업시간 내에서 예약 가능한 구간들)", nullable = true)
            List<TimeRange> bookingTimeRanges
    ) {}

    // 예약 가능 시간대 범위
    @Schema(description = "예약 가능 시간대 범위")
    public record TimeRange(
            @NotNull(message = "시작 시간은 필수입니다")
            @Schema(description = "시작 시간 (HH:mm 형식)",
                    example = "09:00",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String startTime,  // "09:00"

            @NotNull(message = "종료 시간은 필수입니다")
            @Schema(description = "종료 시간 (HH:mm 형식)",
                    example = "10:00",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String endTime     // "10:00"
    ) {}
}