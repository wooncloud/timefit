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
        summary = "업체 구성원 초대",
        description = """
            이메일로 새로운 구성원을 초대합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Request Body 필수값
               - inviteeEmail: 초대할 사용자 이메일
            
            3. 초대 규칙
               - 신규 초대는 무조건 MEMBER 권한으로 생성
               - 권한 상향은 별도 API 사용
               - 이미 구성원인 경우 초대 불가
            
            4. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "구성원 초대 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.MemberListResponse.MemberResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                
                USER_ALREADY_MEMBER - 이미 업체 구성원임
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "INSUFFICIENT_PERMISSION - 권한 부족 (OWNER, MANAGER만 가능)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                
                USER_NOT_FOUND - 초대할 사용자를 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface InviteMemberOperation {
}