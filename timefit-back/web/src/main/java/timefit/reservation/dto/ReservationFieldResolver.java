package timefit.reservation.dto;

import timefit.menu.entity.OrderType;
import timefit.reservation.entity.Reservation;

import java.util.UUID;

/**
 * Reservation → 응답 DTO 필드 변환 전담
 *
 * 역할:
 * - FK(business, menu)가 null인 경우 스냅샷 필드로 fallback하는 규칙을 한 곳에서 관리
 * - DTO의 from() 메서드는 이 클래스에 위임만 함
 *
 * 설계 의도:
 * - Entity는 도메인 상태와 비즈니스 규칙만 담음
 * - 표현을 위한 매핑 규칙(null-safe resolve)은 이 클래스가 책임
 * - 향후 데이터 출처가 변경되어도(외부 API 등) 이 클래스만 수정하면 됨
 */
public class ReservationFieldResolver {

    private ReservationFieldResolver() {}

    // ======================== Business 관련 ========================

    /**
     * 업체 ID
     * business 삭제 시 null 반환 (ID는 스냅샷 없음)
     */
    public static UUID resolveBusinessId(Reservation r) {
        return r.getBusiness() != null ? r.getBusiness().getId() : null;
    }

    /**
     * 업체명
     * business 삭제 시 snapshotBusinessName으로 대체
     */
    public static String resolveBusinessName(Reservation r) {
        return r.getBusiness() != null
                ? r.getBusiness().getBusinessName()
                : r.getSnapshotBusinessName();
    }

    /**
     * 업체 주소
     * business 삭제 시 null 반환
     */
    public static String resolveBusinessAddress(Reservation r) {
        return r.getBusiness() != null ? r.getBusiness().getAddress() : null;
    }

    /**
     * 업체 연락처
     * business 삭제 시 null 반환
     */
    public static String resolveBusinessContactPhone(Reservation r) {
        return r.getBusiness() != null ? r.getBusiness().getContactPhone() : null;
    }

    /**
     * 업체 로고 URL
     * business 삭제 시 null 반환
     */
    public static String resolveBusinessLogoUrl(Reservation r) {
        return r.getBusiness() != null ? r.getBusiness().getLogoUrl() : null;
    }

    // ======================== Menu 관련 ========================

    /**
     * 메뉴 ID
     * menu 삭제 시 null 반환 (ID는 스냅샷 없음)
     */
    public static UUID resolveMenuId(Reservation r) {
        return r.getMenu() != null ? r.getMenu().getId() : null;
    }

    /**
     * 서비스명
     * menu 삭제 시 snapshotMenuName으로 대체
     */
    public static String resolveMenuName(Reservation r) {
        return r.getMenu() != null
                ? r.getMenu().getServiceName()
                : r.getSnapshotMenuName();
    }

    /**
     * 카테고리명
     * menu 삭제 시 null 반환
     */
    public static String resolveMenuCategoryName(Reservation r) {
        return r.getMenu() != null
                ? r.getMenu().getBusinessCategory().getCategoryName()
                : null;
    }

    /**
     * 메뉴 가격
     * menu 삭제 시 reservationPrice(스냅샷)로 대체
     */
    public static Integer resolveMenuPrice(Reservation r) {
        return r.getMenu() != null
                ? r.getMenu().getPrice()
                : r.getReservationPrice();
    }

    /**
     * 메뉴 소요시간
     * menu 삭제 시 reservationDuration(스냅샷)으로 대체
     */
    public static Integer resolveMenuDurationMinutes(Reservation r) {
        return r.getMenu() != null
                ? r.getMenu().getDurationMinutes()
                : r.getReservationDuration();
    }

    /**
     * 메뉴 설명
     * menu 삭제 시 null 반환
     */
    public static String resolveMenuDescription(Reservation r) {
        return r.getMenu() != null ? r.getMenu().getDescription() : null;
    }

    /**
     * 메뉴 주문 타입
     * menu 삭제 시 null 반환
     */
    public static OrderType resolveMenuOrderType(Reservation r) {
        return r.getMenu() != null ? r.getMenu().getOrderType() : null;
    }

    /**
     * 메뉴 이미지 URL
     * menu 삭제 시 null 반환
     */
    public static String resolveMenuImageUrl(Reservation r) {
        return r.getMenu() != null ? r.getMenu().getImageUrl() : null;
    }

    /**
     * 메뉴 활성 여부
     * menu 삭제 시 null 반환
     */
    public static Boolean resolveMenuIsActive(Reservation r) {
        return r.getMenu() != null ? r.getMenu().getIsActive() : null;
    }
}
