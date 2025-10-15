package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.SlotType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

// 메뉴 상세 정보 응답
@Getter
public class MenuResponse {

    private final UUID menuId;
    private final UUID businessId;
    private final String serviceName;
    private final BusinessTypeCode category;
    private final Integer price;
    private final SlotType slotType;
    private final String description;
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
            SlotType slotType,
            String description,
            String imageUrl,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.menuId = menuId;
        this.businessId = businessId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.slotType = slotType;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MenuResponse of(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getBusiness().getId(),
                menu.getServiceName(),
                menu.getCategory(),
                menu.getPrice(),
                menu.getSlotType(),
                menu.getDescription(),
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

