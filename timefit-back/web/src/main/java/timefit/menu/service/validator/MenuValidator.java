package timefit.menu.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.service.validator.BookingSlotValidator;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuValidator {

    private final MenuRepository menuRepository;
    private final BookingSlotValidator bookingSlotValidator;
    private final ReservationRepository reservationRepository;

    /**
     * Menu 존재 여부 검증 및 조회
     *
     * @param menuId 검증할 메뉴 ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException 메뉴가 존재하지 않을 경우
     */
    public Menu validateMenuExists(UUID menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 메뉴 ID: {}", menuId);
                    return new MenuException(MenuErrorCode.MENU_NOT_FOUND);
                });
    }

    /**
     * Menu가 특정 Business에 속하는지 검증
     *
     * @param menu 검증할 Menu 엔티티
     * @param businessId 업체 ID
     * @throws MenuException Menu가 해당 Business에 속하지 않을 경우
     */
    public void validateMenuBelongsToBusiness(Menu menu, UUID businessId) {
        if (!menu.getBusiness().getId().equals(businessId)) {
            log.warn("메뉴가 해당 업체에 속하지 않음: menuId={}, businessId={}",
                    menu.getId(), businessId);
            throw new MenuException(MenuErrorCode.MENU_ACCESS_DENIED);
        }
    }

    /**
     * Menu가 활성 상태인지 검증
     *
     * @param menu 검증할 Menu 엔티티
     * @throws MenuException 비활성 상태일 경우
     */
    public void validateMenuActive(Menu menu) {
        if (!menu.getIsActive()) {
            log.warn("비활성 상태의 메뉴: menuId={}", menu.getId());
            throw new MenuException(MenuErrorCode.MENU_NOT_ACTIVE);
        }
    }

    /**
     * Menu 존재 및 Business 소속 동시 검증 (가장 많이 사용)
     *
     * @param menuId 검증할 메뉴 ID
     * @param businessId 업체 ID
     * @return 조회된 Menu 엔티티
     * @throws MenuException 메뉴가 없거나 해당 업체에 속하지 않을 경우
     */
    public Menu validateMenuOfBusiness(UUID menuId, UUID businessId) {
        Menu menu = validateMenuExists(menuId);
        validateMenuBelongsToBusiness(menu, businessId);
        return menu;
    }

    /**
     * Menu 존재, Business 소속, 활성 상태 모두 검증
     *
     * @param menuId 검증할 메뉴 ID
     * @param businessId 업체 ID
     * @return 조회된 활성 상태의 Menu 엔티티
     */
    public Menu validateActiveMenuOfBusiness(UUID menuId, UUID businessId) {
        Menu menu = validateMenuOfBusiness(menuId, businessId);
        validateMenuActive(menu);
        return menu;
    }



    //   ---------- Menu , BookingSlot 생성 관련

    /**
     * Menu에 미래 활성 예약이 존재하지 않는지 검증
     * - Menu 삭제/비활성화 전에 호출
     * - 오늘 이후의 CANCELLED, NO_SHOW를 제외한 예약이 있으면 예외 발생
     *
     * @param menuId 검증할 메뉴 ID
     * @throws MenuException 미래 활성 예약이 존재할 경우
     */
    public void validateNoFutureActiveReservations(UUID menuId) {
        LocalDate today = LocalDate.now();

        // 미래 예약 조회 (CANCELLED, NO_SHOW 제외)
        List<Reservation> futureReservations = reservationRepository.findAll()
                .stream()
                .filter(r -> r.getMenu().getId().equals(menuId))
                .filter(r -> !r.getReservationDate().isBefore(today))
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED
                        && r.getStatus() != ReservationStatus.NO_SHOW)
                .toList();

        if (!futureReservations.isEmpty()) {
            log.warn("메뉴에 미래 활성 예약 존재: menuId={}, futureReservationsCount={}",
                    menuId, futureReservations.size());
            throw new MenuException(
                    MenuErrorCode.CANNOT_DEACTIVATE_MENU_WITH_RESERVATIONS,
                    String.format("이 메뉴에 %d개의 미래 예약이 존재하여 삭제/비활성화할 수 없습니다. " +
                                    "예약을 먼저 취소하거나 완료 처리해주세요.",
                            futureReservations.size())
            );
        }
    }

    /**
     * Menu 생성 요청 검증
     * - RESERVATION_BASED일 때 durationMinutes 필수
     * - autoGenerateSlots=true일 때 slotSettings 필수
     */
    public void validateMenuCreateRequest(MenuRequestDto.CreateUpdateMenu request) {
        // 1. RESERVATION_BASED 검증
        if (OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(
                        MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION,
                        "예약형 서비스는 소요 시간(durationMinutes)이 필수입니다"
                );
            }
        }

        // 2. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(
                        MenuErrorCode.INVALID_SLOT_SETTINGS,
                        "슬롯 자동 생성 시 슬롯 설정(slotSettings)이 필요합니다"
                );
            }

            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }

    /**
     * Menu 수정 요청 검증
     * - RESERVATION_BASED로 변경 시 durationMinutes 필수
     * - autoGenerateSlots=true일 때 slotSettings 필수
     */
    public void validateMenuUpdateRequest(MenuRequestDto.CreateUpdateMenu request) {
        // 1. RESERVATION_BASED 검증 (변경하는 경우만)
        if (request.orderType() != null &&
                OrderType.RESERVATION_BASED.equals(request.orderType())) {
            if (request.durationMinutes() == null || request.durationMinutes() <= 0) {
                throw new MenuException(
                        MenuErrorCode.DURATION_REQUIRED_FOR_RESERVATION,
                        "예약형 서비스는 소요 시간(durationMinutes)이 필수입니다"
                );
            }
        }

        // 2. BookingSlot 자동 생성 설정 검증
        if (Boolean.TRUE.equals(request.autoGenerateSlots())) {
            if (request.slotSettings() == null) {
                throw new MenuException(
                        MenuErrorCode.INVALID_SLOT_SETTINGS,
                        "슬롯 자동 생성 시 슬롯 설정(slotSettings)이 필요합니다"
                );
            }

            // BookingSlot 관련 검증은 BookingSlotValidator로 위임
            bookingSlotValidator.validateSlotSettings(request.slotSettings());
        }
    }
}