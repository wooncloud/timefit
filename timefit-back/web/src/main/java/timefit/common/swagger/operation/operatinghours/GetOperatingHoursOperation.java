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
        summary = "영업시간 조회",
        description = """
            업체의 영업시간 및 예약 가능 시간대를 조회합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 응답 구조
               - businessId: 업체 ID
               - businessName: 업체명
               - schedules: 요일별 스케줄 목록 (7개 요일)
            
            3. 요일별 스케줄 정보
               - dayOfWeek: 요일 (0=일요일, 1=월요일, ..., 6=토요일)
               - openTime: 영업 시작 시간 (HH:mm)
               - closeTime: 영업 종료 시간 (HH:mm)
               - isClosed: 휴무일 여부
               - bookingTimeRanges: 예약 가능 시간대 목록
            
            4. 권한
               - 인증 불필요 (공개 조회)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
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
                responseCode = "404",
                description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface GetOperatingHoursOperation {
}