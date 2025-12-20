package timefit.common.swagger.operation.invitation;

import io.swagger.v3.oas.annotations.Operation;
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
        summary = "초대 수락",
        description = """
                이메일로 받은 초대를 수락합니다.
                
                1. Request Body
                   - token: 초대 토큰 (필수)
                
                2. 처리 내용
                   - 초대 상태를 ACCEPTED로 변경
                   - UserBusinessRole 생성 (업체 구성원으로 추가)
                
                3. 검증 사항
                   - 토큰 유효성 확인
                   - PENDING 상태인지 확인
                   - 만료되지 않았는지 확인
                   - 사용자 이메일과 초대 이메일 일치 확인
                
                4. 권한
                   - 로그인 필요
                   - 초대받은 이메일과 로그인한 사용자의 이메일이 일치해야 함
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "수락 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = InvitationResponseDto.AcceptResult.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                        VALIDATION_ERROR - token 은(는) 필수입니다
                        
                        INVALID_TOKEN - 유효하지 않은 토큰
                        
                        INVITATION_ALREADY_PROCESSED - 이미 처리된 초대
                        
                        INVITATION_EXPIRED - 만료된 초대
                        
                        EMAIL_MISMATCH - 이메일 불일치 (다른 사람의 초대)
                        """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "USER_NOT_FOUND - 사용자를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface AcceptInvitationOperation {
}