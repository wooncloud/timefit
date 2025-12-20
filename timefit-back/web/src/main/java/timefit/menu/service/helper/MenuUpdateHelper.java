package timefit.menu.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.BusinessCategory;
import timefit.businesscategory.service.validator.BusinessCategoryValidator;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.service.validator.MenuReservationValidator;

import java.util.UUID;

/**
 * Menu 수정(update) 헬퍼
 * - Menu Entity 수정 로직 집중 관리
 * - BusinessCategory 변경
 * - 기본 정보 수정
 * - 소요 시간 수정
 * - 이미지 URL 수정
 * - 활성/비활성 토글
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuUpdateHelper {

    private final BusinessCategoryValidator businessCategoryValidator;
    private final MenuReservationValidator menuReservationValidator;

    /**
     * Menu 데이터 수정 (전체)
     * 1. BusinessCategory 변경
     * 2. 기본 정보 수정
     * 3. 소요 시간 수정
     * 4. 이미지 URL 수정
     *
     * @param menu 수정할 Menu
     * @param request 수정 요청 DTO
     * @param businessId 업체 ID
     */
    public void updateMenuData(
            Menu menu,
            MenuRequestDto.CreateUpdateMenu request,
            UUID businessId) {

        log.debug("Menu 데이터 수정 시작: menuId={}", menu.getId());

        // 1. BusinessCategory 변경
        updateBusinessCategoryIfNeeded(menu, request, businessId);

        // 2. 기본 정보 수정
        updateBasicInfoIfNeeded(menu, request);

        // 3. 소요 시간 수정 (미래 예약 검증 포함)
        updateDurationIfNeeded(menu, request);

        // 4. 이미지 URL 수정
        updateImageUrlIfNeeded(menu, request);

        log.debug("Menu 데이터 수정 완료: menuId={}", menu.getId());
    }

    /**
     * BusinessCategory 변경 (필요 시)
     * 1. businessType 또는 categoryName이 null이면 변경 불필요
     * 2. 변경할 타겟 값 결정 (요청값 우선, 없으면 기존값 유지)
     * 3. BusinessCategory 조회 및 검증
     * 4. 실제로 변경되었을 때만 Entity 업데이트
     *
     * @param menu 수정할 Menu
     * @param request 수정 요청 DTO
     * @param businessId 업체 ID
     */
    private void updateBusinessCategoryIfNeeded(
            Menu menu,
            MenuRequestDto.CreateUpdateMenu request,
            UUID businessId) {

        if (request.businessType() == null && request.categoryName() == null) {
            return;
        }

        BusinessCategory currentCategory = menu.getBusinessCategory();

        // 변경할 타겟 값 결정
        var targetBusinessType = request.businessType() != null
                ? request.businessType()
                : currentCategory.getBusinessType();

        var targetCategoryName = request.categoryName() != null
                ? request.categoryName()
                : currentCategory.getCategoryName();

        // 새로운 BusinessCategory 조회
        BusinessCategory newCategory = businessCategoryValidator.validateAndGetBusinessCategory(
                businessId,
                targetBusinessType,
                targetCategoryName
        );

        // 실제로 변경되었을 때만 업데이트
        if (!newCategory.getId().equals(currentCategory.getId())) {
            menu.updateBusinessCategory(newCategory);
            log.info("BusinessCategory 변경: categoryId={} -> categoryId={}",
                    currentCategory.getId(), newCategory.getId());
        }
    }

    /**
     * 기본 정보 수정 (필요 시)
     * 1. serviceName, price, description 중 하나라도 있으면 수정
     * 2. Entity의 updateBasicInfo 메서드 호출
     *
     * @param menu 수정할 Menu
     * @param request 수정 요청 DTO
     */
    private void updateBasicInfoIfNeeded(Menu menu, MenuRequestDto.CreateUpdateMenu request) {
        if (request.serviceName() != null ||
                request.price() != null ||
                request.description() != null) {

            menu.updateBasicInfo(
                    request.serviceName(),
                    request.price(),
                    request.description()
            );

            log.debug("기본 정보 수정 완료: menuId={}", menu.getId());
        }
    }

    /**
     * 소요 시간 수정 (필요 시)
     * 1. durationMinutes가 null이면 수정 불필요
     * 2. MenuReservationValidator를 통해 미래 예약 검증
     * 3. Entity의 updateDuration 메서드 호출
     * [중요]
     * - 미래 예약이 있으면 수정 불가 (데이터 일관성)
     *
     * @param menu 수정할 Menu
     * @param request 수정 요청 DTO
     * @throws timefit.exception.menu.MenuException 미래 예약이 존재하는 경우
     */
    private void updateDurationIfNeeded(Menu menu, MenuRequestDto.CreateUpdateMenu request) {
        if (request.durationMinutes() == null) {
            return;
        }

        // 미래 예약 검증
        menuReservationValidator.validateActiveReservationsWithDurationMinutes(
                menu.getId(),
                request.durationMinutes(),
                menu.getDurationMinutes()
        );

        menu.updateDuration(request.durationMinutes());
        log.debug("소요 시간 수정: menuId={}, {}분 -> {}분",
                menu.getId(), menu.getDurationMinutes(), request.durationMinutes());
    }

    /**
     * 이미지 URL 수정 (필요 시)
     * 1. imageUrl이 null이면 수정 불필요
     * 2. Entity의 updateImageUrl 메서드 호출
     *
     * @param menu 수정할 Menu
     * @param request 수정 요청 DTO
     */
    private void updateImageUrlIfNeeded(Menu menu, MenuRequestDto.CreateUpdateMenu request) {
        if (request.imageUrl() != null) {
            menu.updateImageUrl(request.imageUrl());
            log.debug("이미지 URL 수정: menuId={}", menu.getId());
        }
    }

    /**
     * 활성/비활성 토글
     * - 활성 → 비활성: 미래 예약 검증 후 비활성화
     * - 비활성 → 활성: 즉시 활성화
     */
    public void toggleActive(Menu menu) {
        if (menu.getIsActive()) {
            // 비활성화 시 미래 예약 검증
            menuReservationValidator.validateActiveReservations(menu.getId());
            menu.deactivate();
            log.info("메뉴 비활성화: menuId={}", menu.getId());
        } else {
            // 활성화
            menu.activate();
            log.info("메뉴 활성화: menuId={}", menu.getId());
        }
    }
}