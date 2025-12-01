package timefit.menu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.common.entity.BaseEntity;

import java.util.UUID;

/**
 * Menu Entity
 * - category (BusinessTypeCode enum) 제거
 * - businessCategory (FK) 추가
 * - 정적 팩토리 메서드 파라미터 변경
 */
@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    /**
     * - 기존: BusinessTypeCode category (enum)
     * - 변경: BusinessCategory FK
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_category_id", nullable = false)
    private BusinessCategory businessCategory;

    @NotBlank(message = "서비스명은 필수입니다")
    @Size(min = 2, max = 100, message = "서비스명은 2자 이상 100자 이하로 입력해주세요")
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 0보다 커야 합니다")
    @Column(nullable = false)
    private Integer price;

    @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "주문 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false, length = 20)
    private OrderType orderType;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // ---------------------- 정적 팩토리 메서드

    // 예약형 메뉴 생성
    public static Menu createReservationBased(
            Business business,
            BusinessCategory businessCategory,
            String serviceName,
            Integer price,
            String description,
            Integer durationMinutes,
            String imageUrl) {

        validateReservationFields(durationMinutes);
        validateBusinessCategoryBelongsToBusiness(business, businessCategory);

        Menu menu = new Menu();
        menu.business = business;
        menu.businessCategory = businessCategory;
        menu.serviceName = serviceName;
        menu.price = price;
        menu.description = description;
        menu.durationMinutes = durationMinutes;
        menu.orderType = OrderType.RESERVATION_BASED;
        menu.imageUrl = imageUrl;
        menu.isActive = true;
        return menu;
    }

    // 주문형 메뉴 생성
    public static Menu createOnDemandBased(
            Business business,
            BusinessCategory businessCategory,
            String serviceName,
            Integer price,
            String description,
            Integer durationMinutes,
            String imageUrl) {

        validateBusinessCategoryBelongsToBusiness(business, businessCategory);

        Menu menu = new Menu();
        menu.business = business;
        menu.businessCategory = businessCategory;
        menu.serviceName = serviceName;
        menu.price = price;
        menu.description = description;
        menu.durationMinutes = durationMinutes;
        menu.orderType = OrderType.ONDEMAND_BASED;
        menu.imageUrl = imageUrl;
        menu.isActive = true;
        return menu;
    }

    // ----------------------- 비즈니스 메서드

    // 카테고리 (중분류) 변경 메서드
    public void updateBusinessCategory(BusinessCategory newCategory) {
        if (newCategory != null) {
            validateBusinessCategoryBelongsToBusiness(this.business, newCategory);
            this.businessCategory = newCategory;
        }
    }

    /**
     * 메뉴 기본 정보 수정
     * 변경: categoryCode 파라미터 제거
     * - businessCategory는 updateBusinessCategory()로 별도 처리
     * - 이 메서드는 serviceName, price, description만 처리
     */
    public void updateBasicInfo(
            String serviceName,
            Integer price,
            String description) {

        if (serviceName != null) {
            this.serviceName = serviceName;
        }
        if (price != null && price > 0) {
            this.price = price;
        }
        if (description != null) {
            this.description = description;
        }
    }

    // 메뉴 이미지 수정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 소요 시간 수정
    public void updateDuration(Integer durationMinutes) {
        if (this.orderType == OrderType.RESERVATION_BASED) {
            validateReservationFields(durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    // 메뉴 활성화
    public void activate() {
        this.isActive = true;
    }

    // 메뉴 비활성화
    public void deactivate() {
        this.isActive = false;
    }

    // 예약형 메뉴인지 확인
    public boolean isReservationBased() {
        return this.orderType == OrderType.RESERVATION_BASED;
    }

    // 주문형 메뉴인지 확인
    public boolean isOnDemandBased() {
        return this.orderType == OrderType.ONDEMAND_BASED;
    }

    // ----------------- 검증 메서드

    // 예약형 메뉴 필수 필드 검증
    private static void validateReservationFields(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("예약형 메뉴는 소요시간이 필수입니다");
        }
    }

    /**
     * BusinessCategory가 Business에 속하는지 검증
     * 기존 validateBusinessCategory() 메서드 대체
     */
    private static void validateBusinessCategoryBelongsToBusiness(
            Business business,
            BusinessCategory businessCategory) {

        if (!businessCategory.getBusiness().getId().equals(business.getId())) {
            throw new IllegalArgumentException(
                    String.format("카테고리가 해당 업체에 속하지 않습니다: businessId=%s, categoryBusinessId=%s",
                            business.getId(),
                            businessCategory.getBusiness().getId())
            );
        }
    }
}