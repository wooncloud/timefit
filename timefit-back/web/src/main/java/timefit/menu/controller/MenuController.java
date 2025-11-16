package timefit.menu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.menu.dto.MenuRequest;
import timefit.menu.dto.MenuResponse;
import timefit.menu.dto.MenuListResponse;
import timefit.menu.service.MenuService;

import java.util.UUID;

/**
 * Menu Controller
 * - 메뉴 CRUD 관리
 * - 업체별 메뉴 조회 (공개 API)
 * - 메뉴 생성/수정/삭제 (인증 필요)
 */
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

//    /**
//     * 메뉴 목록 조회
//     * 권한: 불필요 (공개 API)
//     */
//    @GetMapping
//    public ResponseEntity<ResponseData<MenuListResponse>> getMenuList(
//            @PathVariable UUID businessId) {
//
//        log.info("메뉴 목록 조회 요청: businessId={}", businessId);
//
//        MenuListResponse response = menuService.getMenuList(businessId);
//        return ResponseEntity.ok(ResponseData.of(response));
//    }

    /**
     * 메뉴 상세 조회
     * 권한: 불필요 (공개 API)
     */
    @GetMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponse>> getMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId) {

        log.info("메뉴 상세 조회 요청: businessId={}, menuId={}", businessId, menuId);

        MenuResponse response = menuService.getMenu(businessId, menuId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 목록 조회 (검색/필터링)
     * 권한: 불필요 (공개 API)
     * - serviceName: 서비스명 검색 (부분 일치, 대소문자 무시)
     * - businessCategoryId: 카테고리 ID 필터
     * - minPrice: 최소 가격
     * - maxPrice: 최대 가격
     * - isActive: 활성 상태
     */
    @GetMapping
    public ResponseEntity<ResponseData<MenuListResponse>> getMenuListWithFilters(
            @PathVariable UUID businessId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) UUID businessCategoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean isActive) {

        log.info("메뉴 목록 조회: businessId={}, filters=[name={}, category={}, price={}-{}, active={}]",
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        MenuListResponse response = menuService.getMenuListWithFilters(
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 생성
     * 권한: OWNER, MANAGER
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<MenuResponse>> createMenu(
            @PathVariable UUID businessId,
            @Valid @RequestBody MenuRequest.CreateUpdateMenu request,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 생성 요청: businessId={}, userId={}, serviceName={}, orderType={}, autoGenerateSlots={}",
                businessId, currentUserId, request.serviceName(), request.orderType(), request.autoGenerateSlots());

        MenuResponse response = menuService.createMenu(businessId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 메뉴 수정
     * 권한: OWNER, MANAGER
     */
    @PatchMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponse>> updateMenu(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @Valid @RequestBody MenuRequest.CreateUpdateMenu request,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 수정 요청: businessId={}, menuId={}, userId={}, orderType={}, autoGenerateSlots={}",
                businessId, menuId, currentUserId, request.orderType(), request.autoGenerateSlots());

        MenuResponse response = menuService.updateMenu(businessId, menuId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 메뉴 활성/비활성 토글
     * 권한: OWNER, MANAGER
     * - 현재 활성 상태 → 비활성
     * - 현재 비활성 상태 → 활성
     */
    @PatchMapping("/{menuId}/toggle")
    public ResponseEntity<ResponseData<MenuResponse>> toggleMenuActive(
            @PathVariable UUID businessId,
            @PathVariable UUID menuId,
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 활성상태 토글 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        MenuResponse response = menuService.toggleMenuActive(businessId, menuId, currentUserId);
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