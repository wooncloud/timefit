package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.dto.BookingSlotRequest;
import timefit.booking.service.BookingSlotCommandService;
import timefit.booking.service.dto.AvailableTimeRange;
import timefit.booking.service.dto.DailySlotSchedule;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.businesscategory.service.validator.BusinessCategoryValidator;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.dto.MenuResponseDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.menu.service.validator.MenuValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Menu CUD 전담 서비스
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

    // 메뉴 생성
    public MenuResponseDto.Menu createMenu(
            UUID businessId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 생성 시작: businessId={}, userId={}, serviceName={}",
                businessId, currentUserId, request.serviceName());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Request 검증
        menuValidator.validateMenuCreateRequest(request);

        // 3. BusinessCategory 조회 및 검증
        BusinessCategory businessCategory = getBusinessCategory(
                business,
                request.businessType(),
                request.categoryName()
        );

        // 4. Menu Entity 생성
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

        // 6. BookingSlot 자동 생성
        if (shouldGenerateSlots(request)) {
            createBookingSlotsForMenu(businessId, savedMenu.getId(), request.slotSettings(), currentUserId);
        }

        return MenuResponseDto.Menu.from(savedMenu);
    }

    // BusinessCategory 조회 및 검증
    private BusinessCategory getBusinessCategory(
            Business business,
            BusinessTypeCode businessType,
            String categoryName) {

        return businessCategoryValidator.validateAndGetBusinessCategory(
                business.getId(),
                businessType,
                categoryName
        );
    }

    // 메뉴 수정
    public MenuResponseDto.Menu updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequestDto.CreateUpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 수정 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Request 검증
        menuValidator.validateMenuUpdateRequest(request);

        // 3. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 4. OrderType 변경 시 검증
        OrderType originalOrderType = menu.getOrderType();
        if (request.orderType() != null && !request.orderType().equals(originalOrderType)) {
            log.info("OrderType 변경: {} -> {}", originalOrderType, request.orderType());

            if (request.orderType() == OrderType.RESERVATION_BASED) {
                if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                    throw new MenuException(MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION);
                }
            }

            menu.updateOrderType(request.orderType());
        }

        // 5. businessType 또는 categoryName 변경 시 BusinessCategory 변경
        if (request.businessType() != null || request.categoryName() != null) {
            BusinessCategory currentCategory = menu.getBusinessCategory();

            BusinessTypeCode targetBusinessType =
                    request.businessType() != null ? request.businessType() : currentCategory.getBusinessType();

            String targetCategoryName =
                    request.categoryName() != null ? request.categoryName() : currentCategory.getCategoryName();

            BusinessCategory newCategory = getBusinessCategory(
                    business,
                    targetBusinessType,
                    targetCategoryName
            );

            if (!newCategory.getId().equals(currentCategory.getId())) {
                menu.updateBusinessCategory(newCategory);
                log.info("BusinessCategory 변경: categoryId={} -> categoryId={}",
                        currentCategory.getId(), newCategory.getId());
            }
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

        // 9. BookingSlot 재생성
        if (shouldGenerateSlots(request)) {
            createBookingSlotsForMenu(businessId, menuId, request.slotSettings(), currentUserId);
        }

        return MenuResponseDto.Menu.from(menu);
    }

    // BookingSlot 자동 생성
    private void createBookingSlotsForMenu(
            UUID businessId,
            UUID menuId,
            MenuRequestDto.BookingSlotSettings settings,
            UUID currentUserId) {

        log.info("BookingSlot 자동 생성 시작: menuId={}, startDate={}, endDate={}",
                menuId, settings.startDate(), settings.endDate());

        try {
            List<DailySlotSchedule> schedules = createDailySlots(
                    settings.startDate(),
                    settings.endDate(),
                    settings.specificTimeRanges()
            );

            BookingSlotRequest.BookingSlot slotRequest = new BookingSlotRequest.BookingSlot(
                    menuId,
                    settings.slotIntervalMinutes(),
                    schedules
            );

            bookingSlotCommandService.createSlots(businessId, slotRequest, currentUserId);

            log.info("BookingSlot 자동 생성 완료: menuId={}", menuId);

        } catch (Exception e) {
            log.warn("BookingSlot 자동 생성 실패 (메뉴는 정상 생성됨): menuId={}, error={}",
                    menuId, e.getMessage());
        }
    }

    // BookingSlot 생성 필요 여부 확인
    private boolean shouldGenerateSlots(MenuRequestDto.CreateUpdateMenu request) {
        return request.orderType() == OrderType.RESERVATION_BASED &&
                Boolean.TRUE.equals(request.autoGenerateSlots()) &&
                request.slotSettings() != null;
    }

    // 날짜 범위에 대한 DailySlot 생성
    private List<DailySlotSchedule> createDailySlots(
            LocalDate startDate,
            LocalDate endDate,
            List<MenuRequestDto.TimeRange> specificTimeRanges) {

        List<DailySlotSchedule> dailySlots = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<AvailableTimeRange> timeRanges = new ArrayList<>();

            if (specificTimeRanges != null && !specificTimeRanges.isEmpty()) {
                for (MenuRequestDto.TimeRange tr : specificTimeRanges) {
                    timeRanges.add(AvailableTimeRange.of(
                            LocalTime.parse(tr.startTime()),
                            LocalTime.parse(tr.endTime())
                    ));
                }
            }

            dailySlots.add(DailySlotSchedule.of(date, timeRanges));
        }

        return dailySlots;
    }

    // 메뉴 삭제 (논리 삭제)
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