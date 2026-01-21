package timefit.reservation.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.exception.booking.BookingErrorCode;
import timefit.exception.booking.BookingException;
import timefit.exception.menu.MenuErrorCode;
import timefit.exception.menu.MenuException;
import timefit.menu.entity.Menu;
import timefit.reservation.repository.ReservationQueryRepository;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.UUID;

/**
 * Reservation 엔티티 조회 전담 클래스
 *
 * 역할:
 * - N+1 방지를 위한 fetch join 조회
 * - 엔티티 존재 여부 검증
 * - 단일 책임: "엔티티 로드"만 담당
 *
 * Menu 패턴과 동일:
 * - 순수하게 조회만 수행
 * - 비즈니스 검증은 Validator에서
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEntityLoader {

    private final UserRepository userRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    /**
     * User 조회
     *
     * @param userId 사용자 ID
     * @return User 엔티티
     * @throws AuthException 사용자가 존재하지 않을 경우
     */
    public User loadUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 없음: userId={}", userId);
                    return new AuthException(AuthErrorCode.USER_NOT_FOUND);
                });
    }

    /**
     * BookingSlot 조회 (Business + Menu fetch join)
     * N+1 방지: 연관 엔티티를 한 번에 로드
     *
     * @param bookingSlotId 슬롯 ID
     * @return BookingSlot (Business, Menu 포함)
     * @throws BookingException 슬롯이 존재하지 않을 경우
     */
    public BookingSlot loadBookingSlotWithRelations(UUID bookingSlotId) {
        return reservationQueryRepository.findBookingSlotWithBusinessAndMenu(bookingSlotId)
                .orElseThrow(() -> {
                    log.warn("슬롯 없음: bookingSlotId={}", bookingSlotId);
                    return new BookingException(BookingErrorCode.AVAILABLE_SLOT_NOT_FOUND);
                });
    }

    /**
     * Menu 조회 (Business fetch join)
     * N+1 방지: Business를 함께 로드
     *
     * @param menuId 메뉴 ID
     * @return Menu (Business 포함)
     * @throws MenuException 메뉴가 존재하지 않을 경우
     */
    public Menu loadMenuWithBusiness(UUID menuId) {
        return reservationQueryRepository.findMenuWithBusiness(menuId)
                .orElseThrow(() -> {
                    log.warn("메뉴 없음: menuId={}", menuId);
                    return new MenuException(MenuErrorCode.MENU_NOT_FOUND);
                });
    }
}