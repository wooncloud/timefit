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
        summary = "예약 슬롯 삭제 (업체용)",
        description = """
            특정 슬롯을 영구적으로 삭제합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
               - slotId: 슬롯 ID (UUID)
            
            2. 제약사항
               - 활성 예약이 없는 슬롯만 삭제 가능
               - 예약이 있는 슬롯은 먼저 비활성화 권장
            
            3. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "슬롯 삭제 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "AVAILABLE_SLOT_NOT_MODIFIABLE - 활성 예약 존재로 삭제 불가",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
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
                description = """
                AVAILABLE_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface DeleteSlotOperation {
}