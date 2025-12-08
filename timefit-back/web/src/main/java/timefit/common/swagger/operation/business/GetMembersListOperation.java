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
        summary = "업체 구성원 목록 조회",
        description = """
            업체에 소속된 모든 구성원 목록을 조회합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 제공 정보
               - 구성원 기본 정보 (ID, 이메일, 이름)
               - 권한 (OWNER, MANAGER, MEMBER)
               - 가입일시
               - 활성화 여부
               - 초대자 이름
               - 마지막 로그인 시간
            
            3. 권한
               - OWNER, MANAGER, MEMBER (해당 업체 구성원)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "구성원 목록 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.MemberListResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "INSUFFICIENT_PERMISSION - 권한 부족 (해당 업체 구성원 아님)",
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
public @interface GetMembersListOperation {
}