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
        summary = "내 업체 목록 조회",
        description = """
            현재 로그인한 사용자가 소속된 모든 업체 목록을 조회합니다.
            
            1. 제공 정보
               - 업체 기본 정보 (ID, 이름, 업종, 로고)
               - 내 권한 (OWNER, MANAGER, MEMBER)
               - 가입일시
               - 활성화 여부
            
            2. 응답
               - businesses: 업체 목록
               - totalCount: 소속 업체 수
            
            3. 권한
               - 로그인한 사용자 본인
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.BusinessListResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "UNAUTHORIZED - 인증 실패",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface GetMyBusinessesOperation {
}