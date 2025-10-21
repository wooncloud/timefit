package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * 메뉴 상세 정보 응답 DTO
 */
@Getter
public class MenuResponse {

    private final UUID menuId;
    private final UUID businessId;
    private final String serviceName;
    private final BusinessTypeCode category;
    private final Integer price;
    private final String description;
    private final OrderType orderType;

    // ✅ 추가: 서비스 소요 시간
    private final Integer durationMinutes;

    private final String imageUrl;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private MenuResponse(
            UUID menuId,
            UUID businessId,
            String serviceName,
            BusinessTypeCode category,
            Integer price,
            String description,
            OrderType orderType,
            Integer durationMinutes,
            String imageUrl,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.menuId = menuId;
        this.businessId = businessId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.orderType = orderType;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Entity → DTO 변환 (정적 팩토리 메서드)
     */
    public static MenuResponse of(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getBusiness().getId(),
                menu.getServiceName(),
                menu.getCategory(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getOrderType(),
                menu.getDurationMinutes(),  // ✅ 추가
                menu.getImageUrl(),
                menu.getIsActive(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MenuResponse that = (MenuResponse) other;
        return Objects.equals(menuId, that.menuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuId);
    }
}