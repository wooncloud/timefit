package timefit.menu.entity;

import jakarta.validation.constraints.*;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotBlank(message = "서비스명은 필수입니다")
    @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @NotEmpty(message = "최소 1개 이상의 업종을 선택해야 합니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BusinessTypeCode category;
//    private String category;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @NotNull(message = "서비스 타입은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlotType slotType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 1. SLOT_BASED 서비스 생성
    public static Menu createSlotBased(
            Business business,
            String serviceName,
            BusinessTypeCode category,
            Integer price,
            String description,
            String imageUrl) {

        Menu menu = new Menu();
        menu.business = business;
        menu.serviceName = serviceName;
        menu.category = category;
        menu.price = price;
        menu.description = description;
        menu.imageUrl = imageUrl;
        menu.slotType = SlotType.SLOT_BASED;
        menu.isActive = true;
        return menu;
    }

    // 2. 서비스 정보 업데이트
    public void updateInfo(
            String serviceName,
            BusinessTypeCode category,
            Integer price,
            String description,
            String imageUrl) {

        if (serviceName != null) {
            this.serviceName = serviceName;
        }
        if (category != null) {
            this.category = category;
        }
        if (price != null) {
            this.price = price;
        }
        if (description != null) {
            this.description = description;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }

    // 3. 서비스 비활성화 (논리적 삭제)
    public void deactivate() {
        this.isActive = false;
    }

    // 4. 서비스 활성화
    public void activate() {
        this.isActive = true;
    }

    // 5. 서비스 활성 상태 확인
    public boolean isActiveService() {
        return Boolean.TRUE.equals(this.isActive);
    }

    // 6. SLOT_BASED 타입인지 확인
    public boolean isSlotBased() {
        return this.slotType == SlotType.SLOT_BASED;
    }

    // 7. ORDER_BASED 타입인지 확인
    public boolean isOrderBased() {
        return this.slotType == SlotType.ORDER_BASED;
    }
}