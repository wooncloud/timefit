package timefit.reservation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.reservation.entity.Reservation;
import timefit.reservation.repository.ReservationRepository;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservationNumberUtil {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 번호 생성 (RES-20240615-001 형식)
     */
    public String generateReservationNumber(Reservation reservation) {
        return this.generateReservationNumber(reservation.getReservationDate());
    }

    /**
     * 특정 날짜의 예약 번호 생성
     */
    private String generateReservationNumber(LocalDate reservationDate) {
        String dateStr = reservationDate.toString().replace("-", "");
        long sequence = reservationRepository.countByReservationDate(reservationDate) + 1;
        return String.format("RES-%s-%03d", dateStr, sequence);
    }
}