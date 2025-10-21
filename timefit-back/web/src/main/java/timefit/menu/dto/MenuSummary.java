package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.Objects;
import java.util.UUID;

/**
 * 메뉴 목록 응답 DTO (간략 정보)
 */
@Getter
public class MenuSummary {

    private final UUID menuId;
    private final String serviceName;
    private final BusinessTypeCode category;
    private final Integer price;
    private final OrderType orderType;

    // ✅ 추가: 소요 시간 (목록에서도 표시하면 유용)
    private final Integer durationMinutes;

    private final String imageUrl;
    private final Boolean isActive;

    private MenuSummary(
            UUID menuId,
            String serviceName,
            BusinessTypeCode category,
            Integer price,
            OrderType orderType,
            Integer durationMinutes,
            String imageUrl,
            Boolean isActive) {

        this.menuId = menuId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.orderType = orderType;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    /**
     * Entity → DTO 변환 (정적 팩토리 메서드)
     */
    public static MenuSummary of(Menu menu) {
        return new MenuSummary(
                menu.getId(),
                menu.getServiceName(),
                menu.getCategory(),
                menu.getPrice(),
                menu.getOrderType(),
                menu.getDurationMinutes(),  // ✅ 추가
                menu.getImageUrl(),
                menu.getIsActive()
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MenuSummary that = (MenuSummary) other;
        return Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}