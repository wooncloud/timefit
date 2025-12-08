package timefit.common.swagger.operation.booking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "과거 슬롯 일괄 삭제 (업체용)",
        description = """
            현재 날짜 이전의 모든 슬롯을 일괄 삭제합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 삭제 조건
               - slotDate < 오늘
               - 모든 상태의 슬롯 (활성/비활성)
            
            3. 응답
               - 삭제된 슬롯 수 (Integer)
            
            4. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "삭제된 슬롯 수 반환",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = Integer.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
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
public @interface DeletePastSlotsOperation {
}