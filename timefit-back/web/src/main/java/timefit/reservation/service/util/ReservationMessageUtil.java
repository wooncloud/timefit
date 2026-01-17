package timefit.reservation.service.util;

import org.springframework.stereotype.Component;

/**
 * Reservation 메시지 생성 유틸리티
 *
 * 역할:
 * - 예약 액션 결과 메시지 생성
 * - 순수 계산 로직만 수행 (외부 의존성 없음)
 *
 * Menu 패턴 준수:
 * - 단일 책임: "메시지 생성"만 담당
 * - 순수 함수: 입력 → 출력
 * - private 메서드를 util로 추출
 */
@Component
public class ReservationMessageUtil {

    /**
     * 취소 메시지 생성
     *
     * @param reason 취소 사유 (optional)
     * @return 취소 메시지
     */
    public String buildCancelMessage(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            return "취소되었습니다: " + reason;
        }
        return "취소되었습니다";
    }

    /**
     * 거절 메시지 생성
     *
     * @param reason 거절 사유 (optional)
     * @return 거절 메시지
     */
    public String buildRejectMessage(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            return "거절되었습니다: " + reason;
        }
        return "거절되었습니다";
    }

    /**
     * 완료 메시지 생성
     *
     * @param notes 완료 메모 (optional)
     * @return 완료 메시지
     */
    public String buildCompleteMessage(String notes) {
        String message = "서비스가 완료되었습니다";
        if (notes != null && !notes.trim().isEmpty()) {
            message += " - " + notes;
        }
        return message;
    }

    /**
     * 노쇼 메시지 생성
     *
     * @param notes 노쇼 메모 (optional)
     * @return 노쇼 메시지
     */
    public String buildNoShowMessage(String notes) {
        String message = "노쇼 처리되었습니다";
        if (notes != null && !notes.trim().isEmpty()) {
            message += " - " + notes;
        }
        return message;
    }

    /**
     * 승인 메시지 생성
     *
     * @return 승인 메시지
     */
    public String buildApproveMessage() {
        return "승인되었습니다";
    }
}