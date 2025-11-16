package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.service.BookingSlotCommandService;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.business.service.validator.BusinessCategoryValidator;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.businesscategory.BusinessCategoryErrorCode;
import timefit.exception.businesscategory.BusinessCategoryException;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequest;
import timefit.menu.dto.MenuResponse;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.menu.service.validator.MenuValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Menu CUD 전담 서비스
 * - 생성, 수정, 삭제 (논리 삭제)
 * - BookingSlot 자동 생성 로직 포함
 * - 삭제/비활성화 시 미래 예약 검증 추가
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final BusinessValidator businessValidator;
    private final BusinessCategoryValidator businessCategoryValidator;
    private final MenuValidator menuValidator;
    private final BookingSlotCommandService bookingSlotCommandService;

    /**
     * 메뉴 생성
     * - OrderType 검증
     * - RESERVATION_BASED일 때 durationMinutes 필수
     * - autoGenerateSlots=true일 때 BookingSlot 자동 생성
     */
    public MenuResponse createMenu(
            UUID businessId,
            MenuRequest.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 생성 시작: businessId={}, userId={}, serviceName={}",
                businessId, currentUserId, request.serviceName());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Request 검증 (Validator 에서 처리)
        menuValidator.validateMenuCreateRequest(request);

        // 3. BusinessCategory 조회 및 검증
        BusinessCategory businessCategory = getBusinessCategory(
                business,
                request.businessType(),
                request.categoryCode()
        );

        // 4. Menu Entity 생성 (정적 팩토리)
        Menu menu;
        if (OrderType.RESERVATION_BASED.equals(request.orderType())) {
            menu = Menu.createReservationBased(
                    business,
                    businessCategory,
                    request.serviceName(),
                    request.price(),
                    request.description(),
                    request.durationMinutes(),
                    request.imageUrl()
            );
        } else {
            menu = Menu.createOnDemandBased(
                    business,
                    businessCategory,
                    request.serviceName(),
                    request.price(),
                    request.description(),
                    request.durationMinutes(),
                    request.imageUrl()
            );
        }

        // 5. 저장
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 생성 완료: menuId={}, serviceName={}", savedMenu.getId(), savedMenu.getServiceName());

        // 6. BookingSlot 자동 생성 (RESERVATION_BASED + autoGenerateSlots=true)
        if (shouldGenerateSlots(request)) {
            createBookingSlotsForMenu(businessId, savedMenu.getId(), request.slotSettings(), currentUserId);
        }

        return MenuResponse.from(savedMenu);
    }

    /**
     * BusinessCategory 조회 및 검증
     * - BusinessCategory는 미리 존재해야 함
     * - 없으면 예외 발생
     */
    private BusinessCategory getBusinessCategory(
            Business business,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode) {

        return businessCategoryValidator.validateAndGetBusinessCategory(
                business.getId(),
                businessType,
                categoryCode
        );
    }

    /**
     * 메뉴 수정
     * - OrderType 변경 시 검증
     * - businessType, categoryCode 수정 시 BusinessCategory 변경
     * - BookingSlot 재생성 옵션
     */
    public MenuResponse updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequest.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 수정 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Request 검증 (Validator 에서 처리)
        menuValidator.validateMenuUpdateRequest(request);

        // 3. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 4. OrderType 변경 시 검증
        OrderType originalOrderType = menu.getOrderType();
        if (request.orderType() != null && !request.orderType().equals(originalOrderType)) {
            log.info("OrderType 변경: {} -> {}", originalOrderType, request.orderType());

            // ONDEMAND → RESERVATION 변경 시 durationMinutes 필수
            if (request.orderType() == OrderType.RESERVATION_BASED) {
                if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                    throw new MenuException(MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION);
                }
            }

            // OrderType 업데이트
            menu.updateOrderType(request.orderType());
        }

        // 5. businessType 또는 categoryCode 변경 시 BusinessCategory 변경
        if (request.businessType() != null || request.categoryCode() != null) {
            BusinessCategory currentCategory = menu.getBusinessCategory();
            BusinessTypeCode targetBusinessType =
                    request.businessType() != null ? request.businessType() : currentCategory.getBusinessType();
            ServiceCategoryCode targetCategoryCode =
                    request.categoryCode() != null ? request.categoryCode() : currentCategory.getCategoryCode();

            // businessType 검증
            if (!business.getBusinessTypes().contains(targetBusinessType)) {
                throw new BusinessCategoryException(BusinessCategoryErrorCode.CATEGORY_NOT_FOUND);
            }

            // categoryCode 검증
            businessCategoryValidator.validateCategoryCodeBelongsToBusinessType(
                    targetBusinessType,
                    targetCategoryCode
            );

            // BusinessCategory 찾기
            BusinessCategory newCategory = getBusinessCategory(
                    business,
                    targetBusinessType,
                    targetCategoryCode
            );

            // Menu의 BusinessCategory 변경
            menu.updateBusinessCategory(newCategory);
        }

        // 6. 기본 정보 수정
        if (request.serviceName() != null ||
                request.price() != null ||
                request.description() != null) {
            menu.updateBasicInfo(
                    request.serviceName(),
                    request.price(),
                    request.description()
            );
        }

        // 7. 소요 시간 수정
        if (request.durationMinutes() != null) {
            menu.updateDuration(request.durationMinutes());
        }

        // 8. 이미지 URL 수정
        if (request.imageUrl() != null) {
            menu.updateImageUrl(request.imageUrl());
        }

        log.info("메뉴 수정 완료: menuId={}", menuId);

        // 9. BookingSlot 재생성 (RESERVATION_BASED + autoGenerateSlots=true)
        if (shouldGenerateSlots(request)) {
            // 기존 슬롯은 유지하고 새로운 기간에 대해서만 생성
            createBookingSlotsForMenu(businessId, menuId, request.slotSettings(), currentUserId);
        }

        // 10. DTO 변환
        return MenuResponse.from(menu);
    }

    /**
     * BookingSlot 자동 생성 - BookingSlotCommandService로 단순 위임
     * - RESERVATION_BASED 메뉴에 대해서만 슬롯 생성
     * - 영업시간 기반으로 슬롯 자동 생성 (BookingSlotCommandService 에서 처리)
     */
    private void createBookingSlotsForMenu(
            UUID businessId,
            UUID menuId,
            MenuRequest.BookingSlotSettings settings,
            UUID currentUserId) {

        log.info("BookingSlot 자동 생성 시작: menuId={}, startDate={}, endDate={}",
                menuId, settings.startDate(), settings.endDate());

        try {
            // BookingSlot 생성 요청 DTO 구성
            List<BookingSlotRequest.DailyTimeSlot> dailySlots = createDailySlots(
                    settings.startDate(),
                    settings.endDate(),
                    settings.specificTimeRanges()
            );

            // private 생성자 → static factory method 사용
            // Create (UUID menuId, Integer slotInterval, List<DailyTimeSlot> dailyTimeSlots)
            BookingSlotRequest.Create slotRequest = BookingSlotRequest.Create.of(
                    menuId,
                    settings.slotIntervalMinutes(),
                    dailySlots
            );

            // BookingSlotCommandService로 위임
            bookingSlotCommandService.createSlots(businessId, slotRequest, currentUserId);

            log.info("BookingSlot 자동 생성 완료: menuId={}", menuId);

        } catch (Exception e) {
            log.warn("BookingSlot 자동 생성 실패 (메뉴는 정상 생성됨): menuId={}, error={}",
                    menuId, e.getMessage());
            // BookingSlot 생성 실패해도 Menu 생성은 성공으로 처리
            // 추후 수동으로 슬롯 생성 가능
        }
    }

    /**
     * BookingSlot 생성 필요 여부 확인
     */
    private boolean shouldGenerateSlots(MenuRequest.CreateUpdateMenu request) {
        return request.orderType() == OrderType.RESERVATION_BASED &&
                Boolean.TRUE.equals(request.autoGenerateSlots()) &&
                request.slotSettings() != null;
    }

    /**
     * 날짜 범위에 대한 DailySlot 생성
     * - specificTimeRanges가 있으면 사용, 없으면 빈 리스트 (영업시간 전체 사용)
     */
    private List<BookingSlotRequest.DailyTimeSlot> createDailySlots(
            LocalDate startDate,
            LocalDate endDate,
            List<MenuRequest.TimeRange> specificTimeRanges) {

        List<BookingSlotRequest.DailyTimeSlot> dailySlots = new ArrayList<>();

        // 날짜 범위 순회
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<BookingSlotRequest.TimeRange> timeRanges = new ArrayList<>();

            // 특정 시간대가 지정되어 있으면 사용
            if (specificTimeRanges != null && !specificTimeRanges.isEmpty()) {
                for (MenuRequest.TimeRange tr : specificTimeRanges) {
                    // private 생성자 → static factory method 사용
                    timeRanges.add(BookingSlotRequest.TimeRange.of(
                            LocalTime.parse(tr.startTime()),
                            LocalTime.parse(tr.endTime())
                    ));
                }
            }
            // 없으면 빈 리스트 (BookingSlotCommandService가 영업시간 전체 사용)

            // private 생성자 → static factory method 사용
            dailySlots.add(BookingSlotRequest.DailyTimeSlot.of(date, timeRanges));
        }

        return dailySlots;
    }

    // 메뉴 활성/비활성 토글 - 비활성화 시 예약 검증 추가
    public MenuResponse toggleMenuActive(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 활성상태 토글 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 활성상태 토글
        if (menu.getIsActive()) {
            // 비활성화 전에 미래 예약 검증
            menuValidator.validateNoFutureActiveReservations(menuId);

            menu.deactivate();
            log.info("메뉴 비활성화: menuId={}", menuId);
        } else {
            menu.activate();
            log.info("메뉴 활성화: menuId={}", menuId);
        }

        log.info("메뉴 활성상태 토글 완료: menuId={}, isActive={}", menuId, menu.getIsActive());

        return MenuResponse.from(menu);
    }

    // 메뉴 삭제 (논리 삭제) - 삭제 전 예약 검증 추가
    public void deleteMenu(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 삭제 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 삭제 전에 미래 예약 검증
        menuValidator.validateNoFutureActiveReservations(menuId);

        // 4. 비활성화 (논리 삭제)
        menu.deactivate();

        log.info("메뉴 삭제 완료: menuId={}", menuId);
    }
}