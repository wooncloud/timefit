package timefit.menu.controller;

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
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.dto.MenuResponseDto;
import timefit.menu.service.MenuService;

import java.util.UUID;

@Tag(name = "05. 메뉴 관리", description = "메뉴(업체에서 제공하는 서비스) 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * 메뉴 상세 조회
     * 권한: 불필요 (공개 API)
     */
    @Operation(
            summary = "메뉴 상세 조회",
            description = """
                    업체의 특정 메뉴를 상세 조회합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                        - menuId: 메뉴 ID (UUID)
                    
                    2. 권한
                        - 불필요 (공개 API)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuResponseDto.Menu.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                "menuId": "550e8400-e29b-41d4-a716-446655440000",
                                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                "serviceName": "헤어 컷",
                                                "businessCategoryId": "550e8400-e29b-41d4-a716-446655440002",
                                                "businessType": "BD003",
                                                "categoryName": "헤어",
                                                "price": 30000,
                                                "description": "기본 헤어 컷 서비스",
                                                "orderType": "RESERVATION_BASED",
                                                "durationMinutes": 60,
                                                "imageUrl": "https://example.com/image.jpg",
                                                "isActive": true,
                                                "createdAt": "2025-11-23T10:00:00",
                                                "updatedAt": "2025-11-23T15:30:00"
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MENU_NOT_FOUND - 메뉴를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "MENU_ACCESS_DENIED - 해당 업체의 메뉴가 아님",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @GetMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> getMenu(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "메뉴 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID menuId) {

        log.info("메뉴 상세 조회 요청: businessId={}, menuId={}", businessId, menuId);

        MenuResponseDto.Menu response = menuService.getMenu(businessId, menuId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 목록 조회 (검색/필터링)
     * 권한: 불필요 (공개 API)
     *
     * @param serviceName 서비스명 검색 (부분 일치, 대소문자 무시)
     * @param businessCategoryId 카테고리 ID 필터
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @param isActive 활성 상태
     */
    @Operation(
            summary = "메뉴 목록 조회",
            description = """
                    업체의 메뉴를 검색 및 필터링하여 조회합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                    
                    2. Query Parameter (모두 선택)
                        - serviceName: 서비스명 검색 (부분 일치, 대소문자 무시)
                        - businessCategoryId: 카테고리 ID 필터
                        - minPrice: 최소 가격
                        - maxPrice: 최대 가격
                        - isActive: 활성 상태 (true/false)
                    
                    3. 권한
                        - 불필요 (공개 API)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuResponseDto.MenuList.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                "menus": [
                                                    {
                                                    "menuId": "550e8400-e29b-41d4-a716-446655440000",
                                                    "serviceName": "헤어 컷",
                                                    "price": 30000,
                                                    "orderType": "RESERVATION_BASED",
                                                    "isActive": true
                                                    }
                                                ],
                                                "totalCount": 1
                                                }
                                            }
                                            """
                            )
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
    @GetMapping
    public ResponseEntity<ResponseData<MenuResponseDto.MenuList>> getMenuListWithFilters(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "서비스명 검색 (부분 일치, 대소문자 무시)",
                    example = "헤어"
            )
            @RequestParam(required = false) String serviceName,
            @Parameter(
                    description = "카테고리 ID 필터",
                    example = "550e8400-e29b-41d4-a716-446655440002"
            )
            @RequestParam(required = false) UUID businessCategoryId,
            @Parameter(
                    description = "최소 가격",
                    example = "10000"
            )
            @RequestParam(required = false) Integer minPrice,
            @Parameter(
                    description = "최대 가격",
                    example = "50000"
            )
            @RequestParam(required = false) Integer maxPrice,
            @Parameter(
                    description = "활성 상태 (true: 활성, false: 비활성)",
                    example = "true"
            )
            @RequestParam(required = false) Boolean isActive) {

        log.info("메뉴 목록 조회: businessId={}, filters=[name={}, category={}, price={}-{}, active={}]",
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        MenuResponseDto.MenuList response = menuService.getMenuListWithFilters(
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 생성
     * 권한: OWNER, MANAGER
     */
    @Operation(
            summary = "메뉴 생성",
            description = """
                    업체에 새로운 메뉴를 생성합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                    
                    2. Request Body 필수값
                        - businessType: 업종 코드
                        - categoryName: 카테고리명
                        - serviceName: 서비스명
                        - price: 가격
                        - orderType: 서비스 유형
                    
                    3. Request Body 선택값
                        - description: 서비스 설명
                        - imageUrl: 이미지 URL
                        - durationMinutes: 소요 시간 (RESERVATION_BASED일 때 필수)
                        - autoGenerateSlots: 슬롯 자동 생성 여부
                        - slotSettings: 슬롯 생성 설정 (autoGenerateSlots=true일 때 필수)
                    
                    4. 제약사항
                        - orderType이 RESERVATION_BASED일 때 durationMinutes 필수
                        - autoGenerateSlots가 true일 때 slotSettings 필수
                        -  업체에 존재하는 카테고리만 사용 가능
                    
                    5. 권한
                        - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuResponseDto.Menu.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "success": true,
                                                "data": {
                                                "menuId": "550e8400-e29b-41d4-a716-446655440000",
                                                "businessId": "550e8400-e29b-41d4-a716-446655440001",
                                                "serviceName": "헤어 컷",
                                                "price": 30000,
                                                "orderType": "RESERVATION_BASED",
                                                "durationMinutes": 60,
                                                "isActive": true
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
                            1. businessType 은(는) 필수 값입니다.
                            2. categoryName 은(는) 필수 값입니다.
                            3. serviceName 은(는) 필수 값입니다.
                            4. price 은(는) 필수 값입니다.
                            5. orderType 은(는) 필수 값입니다.
                            
                            DURATION_REQUIRED_FOR_RESERVATION - 예약형 서비스는 소요 시간 필수
                            
                            INVALID_SLOT_SETTINGS - 슬롯 자동 생성 시 슬롯 설정 필요
                            
                            CATEGORY_NOT_FOUND - 카테고리를 찾을 수 없음
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> createMenu(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "메뉴 생성 요청",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuRequestDto.CreateUpdateMenu.class),
                            examples = {
                                    @ExampleObject(
                                            name = "예약형 서비스 (슬롯 자동 생성)",
                                            value = """
                                                    {
                                                        "businessType": "BD003",
                                                        "categoryName": "헤어",
                                                        "serviceName": "헤어 컷",
                                                        "price": 30000,
                                                        "description": "기본 헤어 컷 서비스",
                                                        "orderType": "RESERVATION_BASED",
                                                        "durationMinutes": 60,
                                                        "autoGenerateSlots": true,
                                                        "slotSettings": {
                                                        "startDate": "2025-12-01",
                                                        "endDate": "2025-12-31",
                                                        "slotIntervalMinutes": 30,
                                                        "specificTimeRanges": [
                                                            {
                                                            "startTime": "09:00",
                                                            "endTime": "18:00"
                                                            }
                                                        ]
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "현장 주문형 서비스",
                                            value = """
                                                    {
                                                        "businessType": "BD000",
                                                        "categoryName": "메인 메뉴",
                                                        "serviceName": "김치찌개",
                                                        "price": 8000,
                                                        "description": "얼큰한 김치찌개",
                                                        "orderType": "ONDEMAND_BASED"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 생성 요청: businessId={}, userId={}, serviceName={}, orderType={}, autoGenerateSlots={}",
                businessId, currentUserId, request.serviceName(), request.orderType(), request.autoGenerateSlots());

        MenuResponseDto.Menu response = menuService.createMenu(businessId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 메뉴 수정
     * 권한: OWNER, MANAGER
     */
    @Operation(
            summary = "메뉴 수정",
            description = """
                    메뉴 정보를 수정합니다.
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                        - menuId: 메뉴 ID (UUID)
                    
                    2. Request Body (변경할 필드만 입력)
                        - businessType: 업종 코드
                        - categoryName: 카테고리명
                        - serviceName: 서비스명
                        - price: 가격
                        - description: 서비스 설명
                        - imageUrl: 이미지 URL
                        - orderType: 서비스 유형
                        - durationMinutes: 소요 시간
                        - autoGenerateSlots: 슬롯 자동 생성 여부
                        - slotSettings: 슬롯 생성 설정
                    
                    3. 제약사항
                        - orderType을 RESERVATION_BASED로 변경 시 durationMinutes 필수
                        - autoGenerateSlots가 true일 때 slotSettings 필수
                    
                    4. 권한
                        - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuResponseDto.Menu.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            VALIDATION_ERROR - 요청 형식 오류
                            
                            DURATION_REQUIRED_FOR_RESERVATION - 예약형 서비스는 소요 시간 필수
                            
                            INVALID_SLOT_SETTINGS - 슬롯 자동 생성 시 슬롯 설정 필요
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MENU_NOT_FOUND - 메뉴를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            MENU_ACCESS_DENIED - 해당 업체의 메뉴가 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @PatchMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> updateMenu(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "메뉴 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID menuId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "메뉴 수정 요청 (변경할 필드만 입력)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MenuRequestDto.CreateUpdateMenu.class),
                            examples = {
                                    @ExampleObject(
                                            name = "가격과 설명만 변경",
                                            value = """
                                                    {
                                                        "price": 35000,
                                                        "description": "프리미엄 헤어 컷 서비스"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "카테고리 변경",
                                            value = """
                                                    {
                                                        "businessType": "BD003",
                                                        "categoryName": "프리미엄"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 수정 요청: businessId={}, menuId={}, userId={}, orderType={}, autoGenerateSlots={}",
                businessId, menuId, currentUserId, request.orderType(), request.autoGenerateSlots());

        MenuResponseDto.Menu response = menuService.updateMenu(businessId, menuId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 삭제 (비활성화)
     * 권한: OWNER, MANAGER
     */
    @Operation(
            summary = "메뉴 삭제",
            description = """
                    메뉴를 비활성화합니다 (논리 삭제).
                    
                    1. Path Parameter
                        - businessId: 업체 ID (UUID)
                        - menuId: 메뉴 ID (UUID)
                    
                    2. 동작
                        - 메뉴를 비활성 상태로 전환 (isActive = false)
                        - 실제 데이터는 삭제되지 않음
                    
                    3. 제약사항
                        - 미래 활성 예약이 없어야 함
                    
                    4. 권한
                        - OWNER, MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "CANNOT_DEACTIVATE_MENU_WITH_RESERVATIONS - 미래 예약이 있어 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "MENU_NOT_FOUND - 메뉴를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            BUSINESS_ACCESS_DENIED - 업체 접근 권한 없음
                            
                            MENU_ACCESS_DENIED - 해당 업체의 메뉴가 아님
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class)
                    )
            )
    })
    @DeleteMapping("/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMenu(
            @Parameter(
                    description = "업체 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID businessId,
            @Parameter(
                    description = "메뉴 ID",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID menuId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 삭제 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        menuService.deleteMenu(businessId, menuId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}