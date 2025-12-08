package timefit.common.swagger.operation.operatinghours;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.operatinghours.dto.OperatingHoursResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "영업시간 설정 (업체용)",
        description = """
            업체의 영업시간 및 예약 가능 시간대를 설정합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Request Body 필수값
               - schedules: 요일별 스케줄 목록 (7개 요일 전체 필수)
            
            3. 요일별 스케줄 구조
               - dayOfWeek: 요일 (0=일요일 ~ 6=토요일) - 필수
               - openTime: 영업 시작 시간 (HH:mm) - 영업일인 경우 필수
               - closeTime: 영업 종료 시간 (HH:mm) - 영업일인 경우 필수
               - isClosed: 휴무일 여부 - 선택 (기본값: false)
               - bookingTimeRanges: 예약 가능 시간대 목록 - 선택
            
            4. 예약 가능 시간대 구조
               - startTime: 시작 시간 (HH:mm) - 필수
               - endTime: 종료 시간 (HH:mm) - 필수
               - 영업시간 내에 포함되어야 함
            
            5. 제약사항
               - 7개 요일 모두 설정 필요
               - openTime < closeTime
               - 예약 가능 시간대는 영업시간 내에 포함
               - 예약 가능 시간대 중복 불가
            
            6. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "설정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "businessId": "550e8400-e29b-41d4-a716-446655440001",
                            "businessName": "홍길동 미용실",
                            "schedules": [
                              {
                                "dayOfWeek": 0,
                                "openTime": null,
                                "closeTime": null,
                                "isClosed": true,
                                "bookingTimeRanges": []
                              },
                              {
                                "dayOfWeek": 1,
                                "openTime": "09:00",
                                "closeTime": "18:00",
                                "isClosed": false,
                                "bookingTimeRanges": [
                                  {
                                    "startTime": "09:00",
                                    "endTime": "12:00"
                                  },
                                  {
                                    "startTime": "13:00",
                                    "endTime": "18:00"
                                  }
                                ]
                              }
                            ]
                          }
                        }
                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. 7개 요일 모두 설정이 필요합니다.
                2. openTime은 closeTime보다 이전이어야 합니다.
                3. 예약 가능 시간대는 영업시간 내에 포함되어야 합니다.
                4. 예약 가능 시간대가 중복되었습니다.
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "BUSINESS_ACCESS_DENIED - 권한 부족 (OWNER, MANAGER만 가능)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface SetOperatingHoursOperation {
}