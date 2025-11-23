package timefit.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.common.entity.BaseEntity;

import java.util.UUID;

/**
 * BusinessCategory Entity (서비스 중분류)
 * 설계:
 * - Business : BusinessCategory = 1 : N
 * - BusinessCategory : Menu = 1 : N
 * - businessType(대분류) + categoryName(중분류) 조합으로 서비스 성격 정의
 */
@Entity
@Table(name = "business_category",
        indexes = {
                @Index(name = "idx_business_id", columnList = "business_id"),
                @Index(name = "idx_business_type", columnList = "business_type"),
                @Index(name = "idx_category_name", columnList = "category_name")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessCategory extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    /**
     * 대분류 (업종)
     * 예: BD003 (미용/뷰티업)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false, length = 10)
    private BusinessTypeCode businessType;

    /**
     * 중분류 (서비스 카테고리명) - 사용자 입력
     * 예: "컷", "펌", "염색", "드라이 스타일링"
     */
    @NotNull
    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    /**
     * 카테고리별 안내사항
     * 예: "펌 시술 전 샴푸 필수"
     */
    @Column(name = "category_notice", columnDefinition = "TEXT")
    private String categoryNotice;

    // 활성화 여부
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ------------- 정적 팩토리 메서드

    /**
     * BusinessCategory 생성
     *
     * @param business 소속 업체
     * @param businessType 대분류 (업종)
     * @param categoryName 중분류 (사용자 입력)
     * @param categoryNotice 카테고리 안내사항
     * @return 생성된 BusinessCategory
     */
    public static BusinessCategory create(
            Business business,
            BusinessTypeCode businessType,
            String categoryName,
            String categoryNotice) {

        BusinessCategory category = new BusinessCategory();
        category.business = business;
        category.businessType = businessType;
        category.categoryName = categoryName;
        category.categoryNotice = categoryNotice;
        category.isActive = true;

        return category;
    }

    // -------------------- 비즈니스 메서드

    /**
     * 카테고리 정보 수정
     *
     * @param categoryNotice 안내사항
     */
    public void updateInfo(String categoryNotice) {
        if (categoryNotice != null) {
            this.categoryNotice = categoryNotice;
        }
    }

    /**
     * 카테고리명 수정
     *
     * @param categoryName 새로운 카테고리명
     */
    public void updateCategoryName(String categoryName) {
        if (categoryName != null && !categoryName.isBlank()) {
            this.categoryName = categoryName;
        }
    }

    /**
     * 비활성화
     * - 연관된 Menu가 있을 경우
     * Menu도 함께 비활성화 해야 할 수 있음
     */
    public void deactivate() {
        this.isActive = false;
    }

    // 활성화
    public void activate() {
        this.isActive = true;
    }

    /**
     * 활성 상태 확인
     *
     * @return 활성 여부
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    /**
     * 특정 업체에 속하는지 확인
     *
     * @param businessId 업체 ID
     * @return 소속 여부
     */
    public boolean belongsToBusiness(UUID businessId) {
        return this.business.getId().equals(businessId);
    }
}