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
 * - businessType(대분류) + categoryCode(중분류) 조합으로 서비스 성격 정의
 */
@Entity
@Table(name = "business_category",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_business_type_category",
                columnNames = {"business_id", "business_type", "category_code"}
        ),
        indexes = {
                @Index(name = "idx_business_id", columnList = "business_id"),
                @Index(name = "idx_business_type", columnList = "business_type")
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
     * 중분류 (서비스 카테고리)
     * 예: HAIR_CUT (컷), HAIR_PERM (펌)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category_code", nullable = false, length = 50)
    private ServiceCategoryCode categoryCode;

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
     * @param categoryCode 중분류 (서비스 카테고리)
     * @param categoryNotice 카테고리 안내사항
     * @return BusinessCategory
     */
    public static BusinessCategory create(
            Business business,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode,
            String categoryNotice) {

        // 검증: categoryCode가 businessType에 속하는지 확인
        if (!categoryCode.belongsTo(businessType)) {
            throw new IllegalArgumentException(
                    String.format("카테고리 %s는 업종 %s에 속하지 않습니다",
                            categoryCode.getDisplayName(),
                            businessType.getDescription()));
        }

        BusinessCategory category = new BusinessCategory();
        category.business = business;
        category.businessType = businessType;
        category.categoryCode = categoryCode;
        category.categoryNotice = categoryNotice;
        category.isActive = true;

        return category;
    }

    // -------------------- 비즈니스 메서드

    /**
     * 카테고리 안내사항 수정
     *
     * @param categoryNotice 안내사항
     */
    public void updateNotice(String categoryNotice) {
        this.categoryNotice = categoryNotice;
    }

    // 카테고리 활성화
    public void activate() {
        this.isActive = true;
    }

    /**
     * 카테고리 비활성화
     * 주의: 이 카테고리에 속한 Menu가 있을 경우
     * Menu도 함께 비활성화해야 할 수 있음
     */
    public void deactivate() {
        this.isActive = false;
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

    /**
     * 카테고리 표시 이름 조회
     * ServiceCategoryCode의 displayName 반환
     *
     * @return 표시 이름 (예: "컷", "펌")
     */
    public String getCategoryDisplayName() {
        return this.categoryCode.getDisplayName();
    }

    /**
     * 카테고리 설명 조회
     * ServiceCategoryCode의 description 반환
     *
     * @return 상세 설명
     */
    public String getCategoryDescription() {
        return this.categoryCode.getDescription();
    }
}