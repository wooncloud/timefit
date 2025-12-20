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
        summary = "업체 정보 수정",
        description = """
            업체의 기본 정보를 수정합니다.
            
            1. Path Parameter
               - businessId: 업체 ID (UUID)
            
            2. Request Body (모두 선택)
               - businessName: 업체명 (2-100자)
               - businessTypes: 업종 코드 목록 (1-3개)
               - businessNumber: 사업자번호 (형식: 000-00-00000)
               - ownerName: 대표자명 (최대 50자)
               - address: 주소 (최대 200자)
               - contactPhone: 연락처 (최대 20자)
               - description: 업체 설명 (최대 1000자)
               - logoUrl: 로고 이미지 URL
               - businessNotice: 업체 공지사항 (최대 500자)
            
            3. 수정 규칙
               - null이 아닌 필드만 수정
               - null 필드는 기존 값 유지
            
            4. 권한
               - OWNER, MANAGER
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "업체 정보 수정 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. 업체명은 2자 이상 100자 이하여야 합니다.
                2. 업종은 최소 1개, 최대 3개까지 선택 가능합니다.
                3. 사업자번호 형식이 올바르지 않습니다.
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
                description = "BUSINESS_NOT_FOUND - 업체를 찾을 수 없음",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface UpdateBusinessOperation {
}