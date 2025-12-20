package timefit.common.swagger.operation.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.business.dto.BusinessResponseDto;
import timefit.common.ResponseData;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "업체 생성",
        description = """
            새로운 업체를 등록합니다.
            
            1. Request Body 필수값
               - businessName: 업체명 (2-100자)
               - businessTypes: 업종 코드 목록 (1-3개)
               - businessNumber: 사업자번호 (형식: 000-00-00000)
               - address: 주소 (최대 200자)
               - contactPhone: 연락처 (최대 20자)
            
            2. Request Body 선택값
               - ownerName: 대표자명 (최대 50자)
               - description: 업체 설명 (최대 1000자)
               - logoUrl: 로고 이미지 URL
               - businessNotice: 업체 공지사항 (최대 500자)
            
            3. 생성자 권한
               - 생성자는 자동으로 OWNER 권한 부여
            
            4. 권한
               - 로그인한 사용자 누구나
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "업체 생성 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "businessId": "550e8400-e29b-41d4-a716-446655440001",
                            "businessName": "홍길동 미용실",
                            "businessTypes": ["BD008"],
                            "businessNumber": "123-45-67890",
                            "ownerName": "홍길동",
                            "address": "서울특별시 강남구 테헤란로 123",
                            "contactPhone": "02-1234-5678",
                            "description": "20년 경력의 전문 미용실입니다.",
                            "logoUrl": "https://example.com/logo.png",
                            "businessNotice": "영업시간 변경: 평일 10:00-20:00",
                            "myRole": "OWNER",
                            "isActive": true,
                            "createdAt": "2025-11-23T10:00:00"
                          }
                        }
                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. businessName은 2자 이상 100자 이하여야 합니다.
                2. businessTypes는 1개 이상 3개 이하여야 합니다.
                3. 사업자번호 형식이 올바르지 않습니다.
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "409",
                description = "BUSINESS_ALREADY_EXISTS - 이미 등록된 사업자번호",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface CreateBusinessOperation {
}