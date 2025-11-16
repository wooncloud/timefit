package timefit.menu.dto;

import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Menu Response DTO
 * - category (BusinessTypeCode) 제거
 * - businessCategoryId, businessType, categoryCode, categoryName 추가
 */
public record MenuResponse(
        UUID menuId,
        UUID businessId,
        String serviceName,
        UUID businessCategoryId,
        BusinessTypeCode businessType,         // 대분류 (업종)
        ServiceCategoryCode categoryCode,      // 중분류 코드
        String categoryName,                   // 중분류 표시명
        Integer price,
        String description,
        OrderType orderType,
        Integer durationMinutes,
        String imageUrl,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Entity → DTO 변환 (정적 팩토리)
     *
     * @param menu Menu 엔티티
     * @return MenuResponse DTO
     */
    public static MenuResponse from(Menu menu) {
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
}