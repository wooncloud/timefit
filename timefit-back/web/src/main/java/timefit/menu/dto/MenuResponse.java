package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Menu Response DTO
 * - category (BusinessTypeCode) 제거
 * - businessCategoryId, businessType, categoryCode, categoryName 추가
 */
@Getter
public class MenuResponse {

    private final UUID menuId;
    private final UUID businessId;
    private final String serviceName;

    /**
     * ✅ 신규: BusinessCategory 정보
     */
    private final UUID businessCategoryId;
    private final BusinessTypeCode businessType;        // 대분류
    private final ServiceCategoryCode categoryCode;     // 중분류 코드
    private final String categoryName;                  // 중분류 표시명

    private final Integer price;
    private final String description;
    private final OrderType orderType;
    private final Integer durationMinutes;
    private final String imageUrl;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private MenuResponse(
            UUID menuId,
            UUID businessId,
            String serviceName,
            UUID businessCategoryId,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode,
            String categoryName,
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
        this.businessCategoryId = businessCategoryId;
        this.businessType = businessType;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.price = price;
        this.description = description;
        this.orderType = orderType;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity → DTO 변환 (정적 팩토리 메서드)
    public static MenuResponse of(Menu menu) {
        BusinessCategory category = menu.getBusinessCategory();

        return new MenuResponse(
                menu.getId(),
                menu.getBusiness().getId(),
                menu.getServiceName(),
                category.getId(),                       // businessCategoryId
                category.getBusinessType(),             // businessType (대분류)
                category.getCategoryCode(),             // categoryCode (중분류 코드)
                category.getCategoryDisplayName(),      // categoryName (중분류 표시명)
                menu.getPrice(),
                menu.getDescription(),
                menu.getOrderType(),
                menu.getDurationMinutes(),
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