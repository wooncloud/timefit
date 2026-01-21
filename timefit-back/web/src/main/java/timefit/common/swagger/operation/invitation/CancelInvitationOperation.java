package timefit.common.swagger.operation.invitation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "초대 취소 (업체용)",
        description = """
                초대를 취소합니다.
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                   - invitationId: 초대 ID (UUID)
                
                2. 처리 내용
                   - 초대 상태를 CANCELED로 변경
                
                3. 제약사항
                   - PENDING 상태의 초대만 취소 가능
                   - 이미 수락되거나 만료된 초대는 취소 불가
                
                4. 권한
                   - OWNER, MANAGER
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "204",
                description = "취소 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                        INVITATION_ALREADY_PROCESSED - 이미 처리된 초대 (수락됨, 취소됨 등)
                        
                        INVITATION_EXPIRED - 만료된 초대
                        """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = """
                        BUSINESS_ACCESS_DENIED - 권한 없음 (OWNER, MANAGER만 가능)
                        
                        INVITATION_NOT_BELONG_TO_BUSINESS - 해당 업체의 초대가 아님
                        """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                        INVITATION_NOT_FOUND - 초대를 찾을 수 없음
                        
                        BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                        """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
@Parameter(
        name = "businessId",
        description = "업체 ID",
        required = true,
        example = "30000000-0000-0000-0000-000000000001"
)
@Parameter(
        name = "invitationId",
        description = "초대 ID",
        required = true,
        example = "10000000-0000-0000-0000-000000000001"
)
public @interface CancelInvitationOperation {
}