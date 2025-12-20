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
        summary = "구성원 권한 변경",
        description = """
            구성원의 권한을 변경합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
               - userId: 대상 사용자 ID (UUID)
            
            2. Request Body 필수값
               - newRole: 새로운 권한 (MEMBER, MANAGER)
            
            3. 권한 변경 규칙
               - OWNER는 권한 변경 불가
               - OWNER만 권한 변경 가능
               - 본인의 권한은 변경 불가
            
            4. 권한
               - OWNER만 가능
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "권한 변경 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                CANNOT_CHANGE_OWNER_ROLE - OWNER 권한은 변경할 수 없음
                
                CANNOT_CHANGE_SELF_ROLE - 본인의 권한은 변경할 수 없음
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
public @interface ChangeMemberRoleOperation {
}