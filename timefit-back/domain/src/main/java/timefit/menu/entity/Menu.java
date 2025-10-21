package timefit.menu.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Menu Entity
 * - 업체에서 제공하는 서비스/메뉴
 * - BusinessTypeCode로 카테고리 관리 (Business와 일관성 유지)
 * - OrderType 으로 예약형/주문형 구분
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

    @NotNull
    @NotBlank(message = "서비스명은 필수입니다")
    @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @NotNull(message = "최소 1개 이상의 업종을 선택해야 합니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private BusinessTypeCode category;

    @NotNull(message = "가격은 필수입니다")
    @Positive
    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 예약형 메뉴 생성
    public static Menu createReservationBased(Business business, String serviceName, BusinessTypeCode category,
                                              Integer price, String description, Integer durationMinutes,
                                              String imageUrl) {
        validateReservationFields(durationMinutes);
        validateBusinessCategory(business, category);

        Menu menu = new Menu();
        menu.business = business;
        menu.serviceName = serviceName;
        menu.category = category;
        menu.price = price;
        menu.description = description;
        menu.durationMinutes = durationMinutes;
        menu.orderType = OrderType.RESERVATION_BASED;
        menu.imageUrl = imageUrl;
        menu.isActive = true;
        return menu;
    }

    // 주문형 메뉴 생성
    public static Menu createOnDemandBased(Business business, String serviceName, BusinessTypeCode category,
                                           Integer price, String description, Integer durationMinutes,
                                           String imageUrl) {
        validateBusinessCategory(business, category);

        Menu menu = new Menu();
        menu.business = business;
        menu.serviceName = serviceName;
        menu.category = category;
        menu.price = price;
        menu.description = description;
        menu.durationMinutes = durationMinutes;
        menu.orderType = OrderType.ONDEMAND_BASED;
        menu.imageUrl = imageUrl;
        menu.isActive = true;
        return menu;
    }

    // 메뉴 기본 정보 수정
    public void updateBasicInfo(String serviceName, BusinessTypeCode category, Integer price, String description) {
        if (serviceName != null) {
            this.serviceName = serviceName;
        }
        if (category != null) {
            validateBusinessCategory(this.business, category);
            this.category = category;
        }
        if (price != null && price > 0) {
            this.price = price;
        }
        if (description != null) {
            this.description = description;
        }
    }

    // 소요 시간 수정
    public void updateDuration(Integer durationMinutes) {
        if (OrderType.RESERVATION_BASED == this.orderType) {
            validateReservationFields(durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    // 이미지 URL 수정
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // 메뉴 활성화/비활성화
    public void updateActiveStatus(boolean isActive) {
        this.isActive = isActive;
    }

    // 예약형 메뉴인지 확인
    public boolean isReservationBased() {
        return OrderType.RESERVATION_BASED == this.orderType;
    }

    // 주문형 메뉴인지 확인
    public boolean isOnDemandBased() {
        return OrderType.ONDEMAND_BASED == this.orderType;
    }

    // 활성 상태 확인
    public boolean isActiveMenu() {
        return Boolean.TRUE.equals(this.isActive);
    }

    // 소요 시간 설정 여부 확인
    public boolean hasDuration() {
        return this.durationMinutes != null && this.durationMinutes > 0;
    }

    // 특정 카테고리에 속하는지 확인
    public boolean belongsToCategory(BusinessTypeCode targetCategory) {
        return this.category == targetCategory;
    }

    // === 검증 메서드 ===

    private static void validateReservationFields(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new IllegalArgumentException("예약형 메뉴는 소요시간이 필수입니다");
        }
    }

    private static void validateBusinessCategory(Business business, BusinessTypeCode category) {
        if (!business.getBusinessTypes().contains(category)) {
            throw new IllegalArgumentException(
                    String.format("메뉴 카테고리 '%s'는 업체 업종에 포함되지 않습니다", category)
            );
        }
    }
}