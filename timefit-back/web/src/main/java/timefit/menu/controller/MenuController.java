package timefit.menu.controller;

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
    @GetMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> getMenu(
            @PathVariable UUID businessId,
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
    @GetMapping
    public ResponseEntity<ResponseData<MenuResponseDto.MenuList>> getMenuListWithFilters(
            @PathVariable UUID businessId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) UUID businessCategoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> createMenu(
            @PathVariable UUID businessId,
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
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
    @PatchMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> updateMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 수정 요청: businessId={}, menuId={}, userId={}, orderType={}, autoGenerateSlots={}",
                businessId, menuId, currentUserId, request.orderType(), request.autoGenerateSlots());

        MenuResponseDto.Menu response = menuService.updateMenu(businessId, menuId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 활성/비활성 토글
     * 권한: OWNER, MANAGER
     */
    @PatchMapping("/{menuId}/toggle")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> toggleMenuActive(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 활성상태 토글 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        MenuResponseDto.Menu response = menuService.toggleMenuActive(businessId, menuId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 삭제 (비활성화)
     * 권한: OWNER, MANAGER
     */
    @DeleteMapping("/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 삭제 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        menuService.deleteMenu(businessId, menuId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}