package timefit.service.entity;

public enum SlotType {
    SLOT_BASED,    // 시간대 기반 (선착순, capacity 제한)
    ORDER_BASED    // 주문 기반 (무제한)
}