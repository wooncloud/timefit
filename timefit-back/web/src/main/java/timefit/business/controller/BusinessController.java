package timefit.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.service.BusinessService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.UUID;

/**
 * Business Controller
 * 권한:
 * - OWNER: 모든 권한
 * - MANAGER: 업체 정보 조회/수정, 구성원 조회/초대
 * - MEMBER: 업체 정보 조회만 가능
 */
@Tag(name = "02. 업체 관리", description = "업체 생성, 조회, 수정, 삭제 및 구성원 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // ========================================
    // 공개 API (인증 불필요)
    // ========================================

    /**
     * 업체 상세 정보 조회 (공개)
     * 권한: 누구나 조회 가능
     */
    @Operation(
            summary = "업체 공개 상세 조회",
            description = """
                    인증 없이 업체의 기본 정보를 조회합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. 제공 정보
                       - 업체명, 업종, 주소, 연락처, 설명, 로고 등
                       - 민감 정보 제외 (사업자번호, 내부 공지사항 등)
                    
                    3. 권한
                       - 인증 불필요 (공개 조회)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessResponseDto.PublicBusinessResponse.class)
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
    @GetMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.PublicBusinessResponse>> getBusinessDetail(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId) {

        log.info("업체 상세 조회 요청: businessId={}", businessId);

        BusinessResponseDto.PublicBusinessResponse response = businessService.getPublicBusinessDetail(businessId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
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
    @GetMapping("/search")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> searchBusinesses(
            @Parameter(
                    description = "검색 키워드 (업체명, 설명)",
                    example = "미용실"
            )
            @RequestParam(required = false) String keyword,
            @Parameter(
                    description = "업종 코드",
                    example = "BD008"
            )
            @RequestParam(required = false) BusinessTypeCode businessType,
            @Parameter(
                    description = "지역 (주소)",
                    example = "강남구"
            )
            @RequestParam(required = false) String region,
            @Parameter(
                    description = "페이지 번호 (0부터 시작)",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            @Parameter(
                    description = "페이지 크기",
                    example = "20"
            )
            @RequestParam(defaultValue = "20") int size) {

        log.info("업체 검색 요청: keyword={}, businessType={}, region={}, page={}, size={}",
                keyword, businessType, region, page, size);

        BusinessResponseDto.BusinessListResponse response = businessService.searchBusinesses(
                keyword, businessType, region, page, size);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 인증 필요 API
    // ========================================

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
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
    @GetMapping("/my-businesses")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> getMyBusinesses(
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("내 업체 목록 조회 요청: userId={}", currentUserId);

        BusinessResponseDto.BusinessListResponse response = businessService.getMyBusinesses(currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 사업자용 업체 상세 조회 (민감 정보 포함)
     * 권한: OWNER/MANAGER/MEMBER
     */
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
    @GetMapping("/{businessId}/profile")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> getBusinessProfile(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("사업자용 업체 상세 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.getBusinessProfile(
                businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
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
                                                "isActive": true,
                                                "myRole": "OWNER",
                                                "createdAt": "2025-11-23T10:00:00",
                                                "updatedAt": "2025-11-23T10:00:00"
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
                            1. businessName 은(는) 필수 값입니다.
                            2. businessTypes 은(는) 필수 값입니다.
                            3. businessNumber 은(는) 필수 값입니다.
                            4. address 은(는) 필수 값입니다.
                            5. contactPhone 은(는) 필수 값입니다.
                            6. 업체명은 2자 이상 100자 이하여야 합니다.
                            7. 업종은 최소 1개, 최대 3개까지 선택 가능합니다.
                            8. 사업자번호 형식이 올바르지 않습니다.
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
    @PostMapping
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> createBusiness(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업체 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessRequestDto.CreateBusinessRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "businessName": "홍길동 미용실",
                                              "businessTypes": ["BD008"],
                                              "businessNumber": "123-45-67890",
                                              "ownerName": "홍길동",
                                              "address": "서울특별시 강남구 테헤란로 123",
                                              "contactPhone": "02-1234-5678",
                                              "description": "20년 경력의 전문 미용실입니다.",
                                              "logoUrl": "https://example.com/logo.png",
                                              "businessNotice": "영업시간 변경: 평일 10:00-20:00"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessRequestDto.CreateBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID ownerId) {

        log.info("업체 생성 요청: businessName={}, ownerId={}", request.businessName(), ownerId);

        BusinessResponseDto.BusinessResponse response = businessService.createBusiness(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "BUSINESS_ALREADY_EXISTS - 사업자번호 중복 (다른 업체가 사용 중)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PutMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> updateBusiness(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업체 수정 요청 (모든 필드 선택사항)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessRequestDto.UpdateBusinessRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "businessName": "홍길동 헤어샵",
                                              "description": "30년 경력의 프리미엄 헤어샵입니다."
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessRequestDto.UpdateBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.updateBusiness(
                businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @Operation(
            summary = "업체 삭제 (비활성화)",
            description = """
                    업체를 삭제(비활성화) 처리합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                       - deleteReason: 삭제 사유 (최대 500자)
                    
                    3. 삭제 처리
                       - 논리적 삭제 (isActive = false)
                       - 업체 데이터는 보존됨
                       - 구성원은 비활성화되지 않음
                    
                    4. 권한
                       - OWNER만 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "업체 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessResponseDto.DeleteBusinessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. deleteReason 은(는) 필수 값입니다.
                            2. 삭제 사유는 500자 이하여야 합니다.
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "INSUFFICIENT_PERMISSION - 권한 부족 (OWNER만 가능)",
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
    @DeleteMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.DeleteBusinessResponse>> deleteBusiness(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "업체 삭제 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessRequestDto.DeleteBusinessRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "deleteReason": "사업 종료"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessRequestDto.DeleteBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.DeleteBusinessResponse response = businessService.deleteBusiness(
                businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 구성원 관리 API
    // ========================================

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
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
                       - 마지막 로그인 시각
                    
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
    @GetMapping("/{businessId}/members")
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse>> getMembersList(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.MemberListResponse response = businessService.getMembersList(
                businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     * 변경사항: 신규 초대는 무조건 MEMBER로 생성
     */
    @Operation(
            summary = "업체 구성원 초대",
            description = """
                    이메일로 새로운 구성원을 초대합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                       - email: 초대할 사용자의 이메일
                    
                    3. Request Body 선택값
                       - invitationMessage: 초대 메시지 (최대 500자)
                    
                    4. 초대 규칙
                       - 신규 초대는 무조건 MEMBER 권한으로 시작
                       - 이메일에 해당하는 사용자가 존재해야 함
                       - 이미 구성원인 경우 초대 불가
                    
                    5. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "초대 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessResponseDto.MemberListResponse.MemberResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. email 은(는) 필수 값입니다.
                            2. 올바른 이메일 형식이 아닙니다.
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "USER_ALREADY_MEMBER - 이미 업체 구성원임",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping("/{businessId}/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse.MemberResponse>> inviteMember(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "구성원 초대 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessRequestDto.InviteMemberRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "email": "member@example.com",
                                              "invitationMessage": "팀에 합류해주세요!"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessRequestDto.InviteMemberRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID inviterUserId) {

        log.info("구성원 초대 요청: businessId={}, inviterUserId={}, email={}",
                businessId, inviterUserId, request.email());

        BusinessResponseDto.MemberListResponse.MemberResponse response = businessService.inviteMember(
                businessId, request, inviterUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @Operation(
            summary = "구성원 권한 변경",
            description = """
                    구성원의 권한을 변경합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - userId: 대상 사용자 ID (UUID)
                    
                    2. Request Body 필수값
                       - newRole: 변경할 권한 (MANAGER, MEMBER)
                    
                    3. 권한 변경 규칙
                       - OWNER 권한으로는 변경 불가
                       - 본인의 권한은 변경 불가
                       - MANAGER ↔ MEMBER 간 변경 가능
                    
                    4. 권한
                       - OWNER만 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "권한 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            1. newRole 은(는) 필수 값입니다.
                            
                            CANNOT_CHANGE_OWN_ROLE - 본인의 권한은 변경할 수 없음
                            
                            CANNOT_CHANGE_TO_OWNER - OWNER 권한으로 변경할 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "INSUFFICIENT_PERMISSION - 권한 부족 (OWNER만 가능)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            
                            USER_NOT_MEMBER - 구성원을 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PatchMapping("/{businessId}/member/{userId}/role")
    public ResponseEntity<ResponseData<Void>> changeMemberRole(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "대상 사용자 ID",
                    required = true,
                    example = "660e8400-e29b-41d4-a716-446655440002"
            )
            @PathVariable UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "구성원 권한 변경 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BusinessRequestDto.ChangeMemberRoleRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "newRole": "MANAGER"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody BusinessRequestDto.ChangeMemberRoleRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterId={}",
                businessId, userId, request.newRole(), currentUserId);

        businessService.changeMemberRole(businessId, userId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @Operation(
            summary = "구성원 제거 (논리적 삭제)",
            description = """
                    업체 구성원을 제거합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - userId: 대상 사용자 ID (UUID)
                    
                    2. 제거 규칙
                       - 본인은 제거할 수 없음
                       - OWNER는 제거할 수 없음
                       - OWNER는 모든 구성원 제거 가능
                       - MANAGER는 MEMBER만 제거 가능
                    
                    3. 권한
                       - OWNER (모든 구성원 제거)
                       - MANAGER (MEMBER만 제거)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "제거 성공 (NO_CONTENT)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            CANNOT_REMOVE_SELF - 본인을 제거할 수 없음
                            
                            CANNOT_REMOVE_OWNER - OWNER는 제거할 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "INSUFFICIENT_PERMISSION - 권한 부족 (MANAGER가 MANAGER 제거 시도)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                            
                            USER_NOT_MEMBER - 구성원을 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @DeleteMapping("/{businessId}/member/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseData<Void>> removeMember(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "대상 사용자 ID",
                    required = true,
                    example = "660e8400-e29b-41d4-a716-446655440002"
            )
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, requesterUserId);

        businessService.removeMember(businessId, userId, requesterUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 활성화
     * 권한: OWNER, MANAGER
     */
    @Operation(
            summary = "구성원 활성화",
            description = """
                    비활성화된 구성원을 다시 활성화합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - userId: 대상 사용자 ID (UUID)
                    
                    2. 처리 내용
                       - isActive = true로 변경
                       - 업체 접근 및 활동 재개
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "활성화 성공",
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
    @PatchMapping("/{businessId}/member/{userId}/activate")
    public ResponseEntity<ResponseData<Void>> activateMember(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "대상 사용자 ID",
                    required = true,
                    example = "660e8400-e29b-41d4-a716-446655440002"
            )
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.activateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 비활성화
     * 권한: OWNER, MANAGER
     */
    @Operation(
            summary = "구성원 비활성화",
            description = """
                    구성원을 일시적으로 비활성화합니다.
                    
                    1. Path Parameter
                       - businessId: 업체 ID (UUID)
                       - userId: 대상 사용자 ID (UUID)
                    
                    2. 처리 내용
                       - isActive = false로 변경
                       - 업체 접근 및 활동 제한
                       - OWNER는 비활성화할 수 없음
                    
                    3. 권한
                       - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "비활성화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_REMOVE_OWNER - OWNER는 비활성화할 수 없음",
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
                            
                            USER_NOT_MEMBER - 구성원을 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PatchMapping("/{businessId}/member/{userId}/deactivate")
    public ResponseEntity<ResponseData<Void>> deactivateMember(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "대상 사용자 ID",
                    required = true,
                    example = "660e8400-e29b-41d4-a716-446655440002"
            )
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 비활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.deactivateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }
}