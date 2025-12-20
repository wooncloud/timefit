package timefit.common.swagger.operation.business;

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
        summary = "구성원 제거",
        description = """
            업체에서 구성원을 제거합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
               - userId: 대상 사용자 ID (UUID)
            
            2. 제거 규칙
               - OWNER는 제거할 수 없음
               - 본인은 제거할 수 없음
               - MANAGER는 MEMBER만 제거 가능
               - OWNER는 모든 구성원 제거 가능
            
            3. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "구성원 제거 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                CANNOT_REMOVE_SELF - 본인을 제거할 수 없음
                
                CANNOT_REMOVE_OWNER - OWNER는 제거할 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "INSUFFICIENT_PERMISSION - 권한 부족 (MANAGER가 MANAGER 제거 시도)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                
                USER_NOT_MEMBER - 구성원을 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface RemoveMemberOperation {
}