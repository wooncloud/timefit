package timefit.operatinghours.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "영업시간 관리 응답")
public class OperatingHoursResponseDto {

    // 영업시간 + 예약 가능 시간대 통합 조회 결과
    @Schema(description = "영업시간 조회 결과 (영업시간 + 예약 가능 시간대 통합)")
    public record OperatingHours(
            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "홍길동 미용실"
            )
            String businessName,

            @Schema(description = "요일별 영업시간 및 예약 가능 시간대 목록 (7개 요일)")
            List<DaySchedule> schedules
    ) {}

    // 요일별 스케줄 결과
    @Schema(description = "요일별 영업시간 및 예약 가능 시간대")
    public record DaySchedule(
            @Schema(
                    description = "요일 (0=일요일, 1=월요일, 2=화요일, 3=수요일, 4=목요일, 5=금요일, 6=토요일)",
                    example = "1"
            )
            Integer dayOfWeek,

            @Schema(
                    description = "영업 시작 시간 (HH:mm)",
                    example = "09:00",
                    nullable = true
            )
            String openTime,

            @Schema(
                    description = "영업 종료 시간 (HH:mm)",
                    example = "18:00",
                    nullable = true
            )
            String closeTime,

            @Schema(
                    description = "휴무일 여부",
                    example = "false"
            )
            Boolean isClosed,

            @Schema(
                    description = "예약 가능 시간대 목록",
                    nullable = true
            )
            List<TimeRange> bookingTimeRanges
    ) {}

    // 예약 가능 시간대 결과
    @Schema(description = "예약 가능 시간대 범위")
    public record TimeRange(
            @Schema(
                    description = "시작 시간 (HH:mm)",
                    example = "09:00"
            )
            String startTime,

            @Schema(
                    description = "종료 시간 (HH:mm)",
                    example = "12:00"
            )
            String endTime
    ) {}
}