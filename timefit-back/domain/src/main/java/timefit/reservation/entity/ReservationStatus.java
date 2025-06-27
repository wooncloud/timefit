package timefit.reservation.entity;

public enum ReservationStatus {
    PENDING,      // 대기 중
    CONFIRMED,    // 확정됨
    COMPLETED,    // 완료됨
    CANCELLED,    // 취소됨
    NO_SHOW       // 노쇼
}