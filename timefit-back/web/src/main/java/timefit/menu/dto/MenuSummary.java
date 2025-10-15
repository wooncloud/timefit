package timefit.menu.dto;

import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.SlotType;

import java.util.Objects;
import java.util.UUID;

// 메뉴 목록 응답
@Getter
public class MenuSummary {

    private final UUID menuId;
    private final String serviceName;
    private final BusinessTypeCode category;
    private final Integer price;
    private final SlotType slotType;
    private final String imageUrl;
    private final Boolean isActive;

    private MenuSummary(
            UUID menuId,
            String serviceName,
            BusinessTypeCode category,
            Integer price,
            SlotType slotType,
            String imageUrl,
            Boolean isActive) {

        this.menuId = menuId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.slotType = slotType;
        this.imageUrl = imageUrl;
        this.isActive = isActive;
    }

    public static MenuSummary of(Menu menu) {
        return new MenuSummary(
                menu.getId(),
                menu.getServiceName(),
                menu.getCategory(),
                menu.getPrice(),
                menu.getSlotType(),
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
