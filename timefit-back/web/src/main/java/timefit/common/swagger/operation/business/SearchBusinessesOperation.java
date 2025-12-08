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
        summary = "업체 검색 (페이징)",
        description = """
            키워드, 업종, 지역을 조건으로 업체를 검색합니다.
            
            1. Query Parameter (모두 선택)
               - keyword: 검색 키워드 (업체명, 설명에서 검색)
               - businessType: 업종 코드 (BD000 ~ BD013)
               - region: 지역 (주소에서 검색)
               - page: 페이지 번호 (0부터 시작, 기본값: 0)
               - size: 페이지 크기 (기본값: 20)
            
            2. 검색 조건
               - 모든 파라미터는 선택사항
               - 조건이 없으면 전체 업체 조회
               - 조건이 있으면 AND 조건으로 검색
            
            3. 응답
               - businesses: 업체 목록
               - totalCount: 전체 업체 수
            
            4. 권한
               - 인증 불필요 (공개 검색)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "검색 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.BusinessListResponse.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                INVALID_PAGE_NUMBER - 페이지 번호가 올바르지 않음 (음수)
                
                INVALID_PAGE_SIZE - 페이지 크기가 올바르지 않음 (0 이하 또는 100 초과)
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface SearchBusinessesOperation {
}