package timefit.wishlist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import timefit.common.entity.BaseEntity;
import timefit.menu.entity.Menu;
import timefit.user.entity.User;

/**
 * 찜(Wishlist) 엔티티
 *
 * 핵심 기능:
 * - 고객이 관심 있는 메뉴를 찜 목록에 추가
 * - 1명의 고객은 동일 메뉴를 1번만 찜 가능 (UNIQUE 제약)
 * - Menu 삭제 시 Wishlist도 함께 삭제 (CASCADE)
 *
 * 비즈니스 규칙:
 * - (user_id, menu_id) 조합은 유니크해야 함
 * - Menu가 삭제되면 Wishlist도 자동 삭제
 * - 생성 시간만 기록 (수정 불가)
 */
@Entity
@Table(
        name = "wishlist",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_menu",
                columnNames = {"user_id", "menu_id"}
        ),
        indexes = {
                @Index(name = "idx_wishlist_user_created", columnList = "user_id, created_at DESC")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends BaseEntity {

    /**
     * 찜한 사용자
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 찜한 메뉴
     * Menu 삭제 시 Wishlist도 함께 삭제됨 (CASCADE)
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Menu menu;

    // ---------------------- 정적 팩토리 메서드

    /**
     * Wishlist 생성
     *
     * @param user 찜한 사용자
     * @param menu 찜한 메뉴
     * @return 생성된 Wishlist 엔티티
     */
    public static Wishlist create(User user, Menu menu) {
        validateCreateFields(user, menu);

        Wishlist wishlist = new Wishlist();
        wishlist.user = user;
        wishlist.menu = menu;
        return wishlist;
    }

    // ---------------------- 검증 메서드

    /**
     * 찜 생성 시 필수 필드 검증
     */
    private static void validateCreateFields(User user, Menu menu) {
        if (user == null) {
            throw new IllegalArgumentException("사용자는 필수입니다");
        }
        if (menu == null) {
            throw new IllegalArgumentException("메뉴는 필수입니다");
        }
    }
}