package timefit.menu.dto;

import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Menu 응답 DTO
 * - categoryCode (ServiceCategoryCode enum) 제거
 * - categoryName (String) 단일 필드로 변경
 */
public record MenuResponse(
        UUID menuId,
        UUID businessId,
        String serviceName,
        UUID businessCategoryId,
        BusinessTypeCode businessType,
        String categoryName,
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
        return new MenuResponse(
                menu.getId(),
                menu.getBusiness().getId(),
                menu.getServiceName(),
                menu.getBusinessCategory().getId(),
                menu.getBusinessCategory().getBusinessType(),
                menu.getBusinessCategory().getCategoryName(),
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