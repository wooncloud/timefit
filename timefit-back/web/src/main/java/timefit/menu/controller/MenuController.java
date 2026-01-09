package timefit.menu.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.menu.*;
import timefit.common.swagger.requestbody.menu.*;
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

    @GetMenuOperation
    @GetMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> getMenu(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID menuId) {

        log.info("메뉴 상세 조회 요청: businessId={}, menuId={}", businessId, menuId);

        MenuResponseDto.Menu response = menuService.getMenu(businessId, menuId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetMenuListOperation
    @GetMapping
    public ResponseEntity<ResponseData<MenuResponseDto.MenuList>> getMenuListWithFilters(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "서비스명 검색 (부분 일치)", example = "헤어")
            @RequestParam(required = false) String serviceName,
            @Parameter(description = "카테고리 ID", example = "60000000-0000-0000-0000-000000000001")
            @RequestParam(required = false) UUID businessCategoryId,
            @Parameter(description = "최소 가격", example = "10000")
            @RequestParam(required = false) Integer minPrice,
            @Parameter(description = "최대 가격", example = "50000")
            @RequestParam(required = false) Integer maxPrice,
            @Parameter(description = "활성 상태", example = "true")
            @RequestParam(required = false) Boolean isActive) {

        log.info("메뉴 목록 조회: businessId={}, filters=[name={}, category={}, price={}-{}, active={}]",
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        MenuResponseDto.MenuList response = menuService.getMenuListWithFilters(
                businessId, serviceName, businessCategoryId, minPrice, maxPrice, isActive);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    @CreateMenuOperation
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> createMenu(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @CreateMenuRequestBody
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 생성 요청: businessId={}, userId={}, orderType={}, autoGenerateSlots={}",
                businessId, currentUserId, request.orderType(), request.autoGenerateSlots());
        MenuResponseDto.Menu response = menuService.createMenu(businessId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    @UpdateMenuOperation
    @PatchMapping("/{menuId}")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> updateMenu(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID menuId,
            @UpdateMenuRequestBody
            @Valid @RequestBody MenuRequestDto.CreateUpdateMenu request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 수정 요청: businessId={}, menuId={}, userId={}, orderType={}, autoGenerateSlots={}",
                businessId, menuId, currentUserId, request.orderType(), request.autoGenerateSlots());

        MenuResponseDto.Menu response = menuService.updateMenu(businessId, menuId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @ToggleMenuActiveOperation
    @PatchMapping("/{menuId}/toggle")
    public ResponseEntity<ResponseData<MenuResponseDto.Menu>> toggleMenuActive(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID menuId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 활성상태 토글 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        MenuResponseDto.Menu response = menuService.toggleMenuActive(businessId, menuId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @DeleteMenuOperation
    @DeleteMapping("/{menuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "메뉴 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID menuId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("메뉴 삭제 요청: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        menuService.deleteMenu(businessId, menuId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}