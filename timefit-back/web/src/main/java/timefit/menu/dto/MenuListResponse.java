package timefit.menu.dto;

import lombok.Getter;
import timefit.menu.entity.Menu;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class MenuListResponse {

    private final List<MenuSummary> menus;
    private final Integer totalCount;

    private MenuListResponse(List<MenuSummary> menus, Integer totalCount) {
        this.menus = menus;
        this.totalCount = totalCount;
    }

    // 정적 팩토리 메서드
    public static MenuListResponse of(List<Menu> menuEntities) {
        List<MenuSummary> menus = menuEntities.stream()
                .map(MenuSummary::of)
                .collect(Collectors.toList());
        return new MenuListResponse(menus, menus.size());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        MenuListResponse that = (MenuListResponse) other;
        return Objects.equals(menus, that.menus) &&
                Objects.equals(totalCount, that.totalCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menus, totalCount);
    }
}
