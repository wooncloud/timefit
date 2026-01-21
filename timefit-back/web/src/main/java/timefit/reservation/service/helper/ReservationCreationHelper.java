package timefit.reservation.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.booking.entity.BookingSlot;
import timefit.business.entity.Business;
import timefit.menu.entity.Menu;
import timefit.reservation.dto.ReservationRequestDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.service.validator.ReservationValidator;
import timefit.user.entity.User;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Reservation 생성 로직 전담 클래스
 *
 * 역할:
 * - 예약 타입 판별 (Validator 위임)
 * - 엔티티 조회 (EntityLoader 위임)
 * - 비즈니스 검증 (Validator 위임)
 * - Reservation 엔티티 생성
 *
 * Menu 패턴 준수:
 * - 단일 책임: "예약 생성"만 담당
 * - 오케스트레이션: 각 컴포넌트에 위임
 * - 명확한 단계별 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCreationHelper {

    private final ReservationEntityLoader entityLoader;
    private final ReservationValidator validator;

    /**
     * 예약 생성 (RESERVATION_BASED / ONDEMAND_BASED 통합)
     *
     * @param request 예약 생성 요청
     * @param customerId 고객 ID
     * @return 생성된 Reservation 엔티티 (저장 전)
     */
    public Reservation create(ReservationRequestDto.CreateReservation request, UUID customerId) {
        log.debug("예약 생성 시작: customerId={}, businessId={}", customerId, request.businessId());

        // 1. 예약 타입 판별
        boolean isReservationBased = validator.isReservationBased(
                request.bookingSlotId(),
                request.reservationDate(),
                request.reservationTime()
        );

        // 2. 타입에 따른 생성 로직 위임
        if (isReservationBased) {
            return createReservationBased(request, customerId);
        } else {
            return createOnDemandBased(request, customerId);
        }
    }

    /**
     * RESERVATION_BASED 예약 생성
     *
     * 처리 단계:
     * 1. 엔티티 조회 (User + BookingSlot[Business, Menu])
     * 2. 연관관계 검증
     * 3. 날짜 검증
     * 4. 시간대 충돌 검증
     * 5. Reservation 엔티티 생성
     */
    private Reservation createReservationBased(
            ReservationRequestDto.CreateReservation request,
            UUID customerId) {

        log.debug("RESERVATION_BASED 예약 생성: bookingSlotId={}", request.bookingSlotId());

        // 1. 엔티티 조회 (2개 쿼리: User + BookingSlot[Business, Menu])
        User customer = entityLoader.loadUser(customerId);
        BookingSlot bookingSlot = entityLoader.loadBookingSlotWithRelations(request.bookingSlotId());

        // BookingSlot에서 연관 엔티티 가져오기 (이미 fetch join됨)
        Business business = bookingSlot.getBusiness();
        Menu menu = bookingSlot.getMenu();

        // 2. 연관관계 검증 (Menu가 Business에 속하는지)
        validator.validateMenuBelongsToBusiness(menu, business.getId());

        // 3. 날짜 검증
        validator.validateNotPastDate(bookingSlot.getSlotDate());

        // 4. 시간대 충돌 검증
        LocalTime startTime = bookingSlot.getStartTime();
        LocalTime endTime = startTime.plusMinutes(menu.getDurationMinutes());

        validator.validateTimeSlotConflict(
                business.getId(),
                bookingSlot.getSlotDate(),
                startTime,
                endTime,
                menu.getId()
        );

        // 5. Reservation 엔티티 생성
        return Reservation.createReservationBased(
                customer,
                business,
                menu,
                bookingSlot,
                request.customerName(),
                request.customerPhone(),
                request.notes()
        );
    }

    /**
     * ONDEMAND_BASED 예약 생성
     *
     * 처리 단계:
     * 1. 엔티티 조회 (User + Menu[Business])
     * 2. 연관관계 검증
     * 3. 날짜 검증
     * 4. Reservation 엔티티 생성
     */
    private Reservation createOnDemandBased(
            ReservationRequestDto.CreateReservation request,
            UUID customerId) {

        log.debug("ONDEMAND_BASED 예약 생성: menuId={}", request.menuId());

        // 1. 엔티티 조회 (2개 쿼리: User + Menu[Business])
        User customer = entityLoader.loadUser(customerId);
        Menu menu = entityLoader.loadMenuWithBusiness(request.menuId());

        // Menu에서 Business 가져오기 (이미 fetch join됨)
        Business business = menu.getBusiness();

        // 2. 연관관계 검증 (Menu가 Business에 속하는지)
        validator.validateMenuBelongsToBusiness(menu, business.getId());

        // 3. 날짜 검증
        validator.validateNotPastDate(request.reservationDate());

        // 4. Reservation 엔티티 생성 (ONDEMAND는 시간대 충돌 체크 불필요)
        return Reservation.createOnDemandBased(
                customer,
                business,
                menu,
                request.reservationDate(),
                request.reservationTime(),
                request.customerName(),
                request.customerPhone(),
                request.notes()
        );
    }
}