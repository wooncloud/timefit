package timefit.menu.entity;

public enum OrderType {
    /**
     * 예약형 - 사전 예약이 필요한 서비스
     * - 선점 방식 (capacity 1~N)
     * - BookingSlot 생성 필요
     * - 미용실, 병원, 상담 등 서비스에 사용
     */
    RESERVATION_BASED,    // 시간대 기반 (선착순, capacity 제한)

    /**
     * 주문형 - 즉시 주문 가능한 서비스
     * - 무제한 용량 (capacity 0)
     * - BookingSlot 불필요
     * - 음식 배달, 택시 호출 등 서비스에 사용
     */
    ONDEMAND_BASED
}