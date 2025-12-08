package timefit.common.swagger.operation.operatinghours;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.operatinghours.dto.OperatingHoursResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "영업시간 기본값으로 리셋 (업체용)",
        description = """
            업체의 영업시간을 기본값으로 초기화합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 기본값 설정
               - 월~금: 09:00-18:00 (영업)
               - 토~일: 휴무
               - 예약 가능 시간대: 영업시간 전체
            
            3. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "리셋 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OperatingHoursResponseDto.OperatingHours.class)
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
public @interface ResetOperatingHoursOperation {
}