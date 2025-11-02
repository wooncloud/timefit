package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.util.Objects;
import java.util.UUID;

/**
 * 메뉴 목록 응답 DTO (간략 정보)
 * - category (BusinessTypeCode) 제거
 * - businessCategoryId, businessType, categoryCode, categoryName 추가
 */
@Getter
public class MenuSummary {

    private final UUID menuId;
    private final String serviceName;
    private final UUID businessCategoryId;
    private final BusinessTypeCode businessType;        // 대분류
    private final ServiceCategoryCode categoryCode;     // 중분류 코드
    private final String categoryName;                  // 중분류 표시명
    private final Integer price;
    private final OrderType orderType;
    private final Integer durationMinutes;
    private final String imageUrl;
    private final Boolean isActive;

    private MenuSummary(
            UUID menuId,
            String serviceName,
            UUID businessCategoryId,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode,
            String categoryName,
            Integer price,
            OrderType orderType,
            Integer durationMinutes,
            String imageUrl,
            Boolean isActive) {

        this.menuId = menuId;
        this.serviceName = serviceName;
        this.businessCategoryId = businessCategoryId;
        this.businessType = businessType;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.price = price;
        this.orderType = orderType;
        this.durationMinutes = durationMinutes;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    // Entity → DTO 변환 (정적 팩토리 메서드)
    public static MenuSummary of(Menu menu) {
        BusinessCategory category = menu.getBusinessCategory();

        return new MenuSummary(
                menu.getId(),
                menu.getServiceName(),
                category.getId(),                       // businessCategoryId
                category.getBusinessType(),             // businessType (대분류)
                category.getCategoryCode(),             // categoryCode (중분류 코드)
                category.getCategoryDisplayName(),      // categoryName (중분류 표시명)
                menu.getPrice(),
                menu.getOrderType(),
                menu.getDurationMinutes(),
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