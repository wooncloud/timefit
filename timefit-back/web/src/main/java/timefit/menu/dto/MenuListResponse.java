package timefit.menu.dto;

import timefit.menu.entity.Menu;
import java.util.List;

public record MenuListResponse(
        List<MenuResponse> menus,
        Integer totalCount
) {
    /**
     * Entity List → DTO 변환 (정적 팩토리)
     *
     * @param menuEntities Menu 엔티티 리스트
     * @return MenuListResponse DTO
     */
    public static MenuListResponse of(List<Menu> menuEntities) {
        List<MenuResponse> menus = menuEntities.stream()
                .map(MenuResponse::from)
                .toList();

        return new MenuListResponse(menus, menus.size());
    }
}