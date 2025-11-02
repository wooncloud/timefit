package timefit.reservation.service.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 예약 번호 생성 유틸리티
 *
 * 형식: RES-20251020143052-A1B2C3
 * - RES: 예약 식별자
 * - 20251020143052: 생성 시각 (yyyyMMddHHmmss)
 * - A1B2C3: 랜덤 6자리 (충돌 방지)
 */
@Component
public class ReservationNumberUtil {

    private static final String PREFIX = "RES";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int RANDOM_LENGTH = 6;

    /**
     * 예약 번호 생성
     *
     * @return 생성된 예약 번호 (예: RES-20251020143052-A1B2C3)
     */
    public String generate() {
        String datePart = LocalDateTime.now().format(DATE_FORMATTER);
        String randomPart = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, RANDOM_LENGTH)
                .toUpperCase();

        return String.format("%s-%s-%s", PREFIX, datePart, randomPart);
    }
}