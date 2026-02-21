package timefit.reservation.service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Reservation Pageable 생성 유틸리티
 * - Pageable 객체 생성
 * - 정렬 규칙 중앙 관리
 */
public class ReservationPageableUtil {

    private ReservationPageableUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 기본 Pageable 생성
     * 정렬: createdAt DESC (최신순)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return Pageable 객체
     */
    public static Pageable createDefault(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}