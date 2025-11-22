package timefit.menu.dto;

import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MenuResponseDto {

    /**
     * 메뉴 상세 정보
     * - 단일 메뉴 조회, 생성/수정 응답에 사용
     */
    public record Menu(
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
        // Entity → DTO 변환
        public static Menu from(timefit.menu.entity.Menu menu) {
            return new Menu(
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

    /**
     * 메뉴 목록 응답
     * - Menu를 재사용하여 DTO 개수 최소화
     */
    public record MenuList(
            List<Menu> menus,
            Integer totalCount
    ) {
        // Entity List → DTO 변환
        public static MenuList of(List<timefit.menu.entity.Menu> menuEntities) {
            List<Menu> menus = menuEntities.stream()
                    .map(Menu::from)
                    .toList();

            return new MenuList(menus, menus.size());
        }
    }
}