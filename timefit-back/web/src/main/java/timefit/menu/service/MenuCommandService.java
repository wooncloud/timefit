package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.service.validator.BusinessValidator;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.dto.MenuResponseDto;
import timefit.menu.entity.Menu;
import timefit.menu.repository.MenuRepository;
import timefit.menu.service.helper.MenuBookingSlotHelper;
import timefit.menu.service.factory.MenuEntityFactory;
import timefit.menu.service.helper.MenuUpdateHelper;
import timefit.menu.service.validator.MenuReservationValidator;
import timefit.menu.service.validator.MenuValidator;

import java.util.UUID;

/**
 * [위임 구조]
 * - MenuValidator: 기본 검증
 * - MenuReservationValidator: 예약 관련 검증
 * - MenuEntityFactory: Menu Entity 생성
 * - MenuUpdateHelper: Menu Entity 수정
 * - MenuBookingSlotAdapter: BookingSlot 생성 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final BusinessValidator businessValidator;
    private final MenuValidator menuValidator;
    private final MenuReservationValidator menuReservationValidator;
    private final MenuEntityFactory menuEntityFactory;
    private final MenuUpdateHelper menuUpdateHelper;
    private final MenuBookingSlotHelper menuBookingSlotHelper;

    /**
     * 메뉴 생성
     * 1. 권한 검증
     * 2. 생성 요청 검증
     * 3. Menu Entity 생성
     * 4. 저장
     * 5. BookingSlot 생성
     * - Menu와 BookingSlot은 하나의 트랜잭션 (실패 시 전체 롤백)
     */
    public MenuResponseDto.Menu createMenu(
            UUID businessId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 생성 시작: businessId={}, userId={}, serviceName={}",
                businessId, currentUserId, request.serviceName());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 생성 요청 검증
        menuValidator.validateMenuCreateRequest(request);

        // 3. Menu Entity 생성
        Menu menu = menuEntityFactory.createMenu(business, request);

        // 4. 저장
        menuRepository.save(menu);
        log.info("메뉴 생성 완료: menuId={}, serviceName={}",
                menu.getId(), menu.getServiceName());

        // 5. BookingSlot 생성 위임
        menuBookingSlotHelper.generateForMenu(menu, request);

        return MenuResponseDto.Menu.from(menu);
    }

    /**
     * 메뉴 수정
     * 1. 권한 검증
     * 2. Menu 조회 및 수정 전 상태 저장
     * 3. 수정 요청 검증
     * 4. Menu 데이터 수정
     * 5. BookingSlot 재생성
     */
    public MenuResponseDto.Menu updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 수정 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 수정 전 상태 저장
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);
        Integer oldDurationMinutes = menu.getDurationMinutes();

        // 3. 수정 요청 검증
        menuValidator.validateMenuUpdateRequest(request, menu);

        // 4. Menu 데이터 수정
        menuUpdateHelper.updateMenuData(menu, request, businessId);
        log.info("메뉴 수정 완료: menuId={}", menuId);

        // 5. BookingSlot 재생성 위임
        menuBookingSlotHelper.regenerateForMenu(menu, request, oldDurationMinutes);

        return MenuResponseDto.Menu.from(menu);
    }

    /**
     * 메뉴 활성/비활성 토글
     * 1. 권한 검증
     * 2. Menu 조회
     * 3. 활성 상태 토글
     */
    public MenuResponseDto.Menu toggleMenuActive(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 활성상태 토글 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 활성 상태 토글
        menuUpdateHelper.toggleActive(menu);

        log.info("메뉴 활성상태 토글 완료: menuId={}, isActive={}",
                menuId, menu.getIsActive());

        return MenuResponseDto.Menu.from(menu);
    }

    /**
     * 메뉴 삭제 (영구 삭제)
     * 1. 권한 검증
     * 2. Menu 조회
     * 3. 미래 예약 검증
     * 4. 영구 삭제
     */
    public void deleteMenu(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 삭제 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 미래 예약 검증
        menuReservationValidator.validateActiveReservations(menuId);

        // 4. 영구 삭제
        menuRepository.delete(menu);

        log.info("메뉴 삭제 완료: menuId={}", menuId);
    }
}