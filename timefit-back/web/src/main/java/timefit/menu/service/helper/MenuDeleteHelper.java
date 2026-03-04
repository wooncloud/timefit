package timefit.menu.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import timefit.booking.entity.BookingSlot;
import timefit.booking.repository.BookingSlotRepository;
import timefit.menu.entity.Menu;
import timefit.menu.repository.MenuRepository;
import timefit.reservation.service.helper.ReservationDetachHelper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuDeleteHelper {

    private final MenuRepository menuRepository;
    private final BookingSlotRepository bookingSlotRepository;
    private final ReservationDetachHelper reservationDetachHelper;

    /**
     * Menu 영구 삭제
     * - 이력 예약(COMPLETED/CANCELLED/NO_SHOW)의 menu FK null 처리 후 삭제
     * - Reservation 행 자체는 보존
     *
     * @param menu 삭제 대상 Menu
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void delete(Menu menu) {
        UUID menuId = menu.getId();
        UUID businessId = menu.getBusiness().getId();

        // 1. 해당 Menu의 BookingSlot ID 목록 조회
        List<BookingSlot> slots = bookingSlotRepository
                .findByBusinessIdAndMenuId(businessId, menuId);

        if (!slots.isEmpty()) {
            List<UUID> slotIds = slots.stream()
                    .map(BookingSlot::getId)
                    .collect(Collectors.toList());

            // 2. 이력 예약의 bookingSlot FK null 처리
            reservationDetachHelper.detachFromBookingSlots(slotIds);
        }

        // 3. 이력 예약의 menu FK null 처리
        reservationDetachHelper.detachFromMenu(menuId);

        // 4. Menu 삭제 (BookingSlot은 DB CASCADE로 함께 삭제됨)
        menuRepository.delete(menu);

        log.debug("메뉴 삭제 완료: menuId={}", menuId);
    }
}