package timefit.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import timefit.common.entity.BaseEntity;
import timefit.user.entity.User;

/**
 * 업체-고객 메모 Entity
 * 핵심 기능:
 * - 사업자가 특정 고객에 대해 작성하는 메모 (업체 단위 관리)
 * - 예: "견과류 알레르기", "단골 고객 - VIP", "매주 월요일 오전 10시 선호"
 * 비즈니스 규칙:
 * - (business_id, customer_id) 조합은 유니크 → 고객 1명당 메모 1개
 * - CASCADE 미적용:
 *   - 업체는 soft delete 방식 → DB row가 실제로 삭제되지 않음
 *   - 메모는 중요도가 낮아 고아 객체가 생겨도 운영상 문제없음
 * - memo는 nullable — 메모 없는 고객도 조회 가능해야 하므로
 *   (고객 목록은 reservation 기반, 메모는 LEFT JOIN으로 가져옴)
 */
@Entity
@Table(
        name = "business_customer_memo",
        uniqueConstraints = @UniqueConstraint(
                // 한 업체에서 한 고객당 메모는 1개만 허용 (DB 레벨 강제)
                // 동시 요청이 들어와도 중복 INSERT를 막기 위해 JPA가 아닌 DB 제약으로 처리
                name = "uk_business_customer_memo",
                columnNames = {"business_id", "customer_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("업체별 고객 메모 테이블")
public class BusinessCustomerMemo extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @Comment("메모가 속한 업체")
    private Business business;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @Comment("메모 대상 고객 (User)")
    private User customer;

    @Column(name = "memo", columnDefinition = "TEXT")
    @Comment("사업자가 작성하는 고객 메모")
    private String memo;

    // ========== 정적 팩토리 메서드 ==========

    public static BusinessCustomerMemo create(Business business, User customer, String memo) {
        BusinessCustomerMemo entity = new BusinessCustomerMemo();
        entity.business = business;
        entity.customer = customer;
        entity.memo = memo;
        return entity;
    }

    // ========== 비즈니스 메서드 ==========

    public void updateMemo(String memo) {
        this.memo = memo;
    }
}