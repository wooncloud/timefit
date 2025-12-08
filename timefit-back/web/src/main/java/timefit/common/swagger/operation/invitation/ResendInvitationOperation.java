package timefit.common.swagger.operation.invitation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.invitation.dto.InvitationResponseDto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "초대 재발송 (업체용)",
        description = """
                초대를 재발송합니다.
                
                1. Path Parameter
                   - businessId: 업체 ID (UUID)
                   - invitationId: 초대 ID (UUID)
                
                2. 처리 내용
                   - 만료 시간 연장
                   - 초대 이메일 재발송
                
                3. 제약사항
                   - PENDING 상태의 초대만 재발송 가능
                   - 만료되지 않은 초대도 재발송 가능
                
                4. 권한
                   - OWNER, MANAGER
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "재발송 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = InvitationResponseDto.Invitation.class)
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
                description = "BUSINESS_ACCESS_DENIED - 권한 없음 (OWNER, MANAGER만 가능)",
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
        example = "550e8400-e29b-41d4-a716-446655440001"
)
@Parameter(
        name = "invitationId",
        description = "초대 ID",
        required = true,
        example = "550e8400-e29b-41d4-a716-446655440000"
)
public @interface ResendInvitationOperation {
}