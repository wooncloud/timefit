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
        summary = "업체 상세 조회 (사업자용)",
        description = """
            업체 구성원만 조회 가능한 상세 정보를 포함합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. 제공 정보 (공개 정보 + 추가)
               - 사업자번호
               - 업체 공지사항 (내부용)
               - 내 권한
               - 활성화 여부
            
            3. 권한
               - OWNER, MANAGER, MEMBER (해당 업체 구성원)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = """
                USER_NOT_BUSINESS_MEMBER - 해당 업체 구성원이 아님
                
                INSUFFICIENT_PERMISSION - 권한 부족
                """,
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
public @interface GetBusinessProfileOperation {
}