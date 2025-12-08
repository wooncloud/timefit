package timefit.common.swagger.operation.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.business.dto.BusinessResponseDto;
import timefit.common.ResponseData;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "업체 삭제 (비활성화)",
        description = """
            업체를 비활성화합니다 (Soft Delete).
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Request Body 필수값
               - confirmationText: 확인 문구 (업체명 입력)
            
            3. 삭제 조건
               - 진행 중인 예약이 없어야 함
               - OWNER 권한 필수
            
            4. 권한
               - OWNER만 가능
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "업체 삭제 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.DeleteBusinessResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 확인 문구가 일치하지 않음
                
                BUSINESS_HAS_ACTIVE_RESERVATIONS - 진행 중인 예약이 있어 삭제 불가
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "INSUFFICIENT_PERMISSION - 권한 부족 (OWNER만 가능)",
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
public @interface DeleteBusinessOperation {
}