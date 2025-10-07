package timefit.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import timefit.common.entity.BaseEntity;

@Entity
@Table(name = "business")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseEntity {

    @NotBlank(message = "상호명은 필수입니다")
    @Size(min = 2, max = 100, message = "상호명은 2자 이상 100자 이하로 입력해주세요")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_type")
    private String businessType;

    @NotBlank(message = "사업자번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
    @Column(name = "business_number", nullable = false)
    private String businessNumber;

    @Size(max = 200, message = "주소는 200자 이하로 입력해주세요")
    private String address;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // 업체 새 생성
    public static Business createBusiness(String businessName, String businessType, String businessNumber,
                                            String address, String contactPhone, String description) {
        Business business = new Business();
        business.businessName = businessName;
        business.businessType = businessType;
        business.businessNumber = businessNumber;
        business.address = address;
        business.contactPhone = contactPhone;
        business.description = description;
        business.isActive = true;
        return business;
    }

    // 업체 전체 정보 업데이트
    public void updateBusinessInfo(String businessName, String businessType, String address,
                                    String contactPhone, String description, String logoUrl) {
        if (businessName != null) {
            this.businessName = businessName;
        }
        if (businessType != null) {
            this.businessType = businessType;
        }
        if (address != null) {
            this.address = address;
        }
        if (contactPhone != null) {
            this.contactPhone = contactPhone;
        }
        if (description != null) {
            this.description = description;
        }
        if (logoUrl != null) {
            this.logoUrl = logoUrl;
        }
    }

    // 업체 로고 업데이트
    public void updateLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    // 업체 기본 정보만 업데이트 (상호명, 업종, 주소)
    public void updateBasicInfo(String businessName, String businessType, String address) {
        if (businessName != null) {
            this.businessName = businessName;
        }
        if (businessType != null) {
            this.businessType = businessType;
        }
        if (address != null) {
            this.address = address;
        }
    }

    // 연락처 정보만 업데이트
    public void updateContactInfo(String contactPhone, String description) {
        if (contactPhone != null) {
            this.contactPhone = contactPhone;
        }
        if (description != null) {
            this.description = description;
        }
    }

    // 업체 비활성화 (논리적 삭제)
    public void deactivate() {
        this.isActive = false;
    }

    // 업체 활성화 (복구)
    public void activate() {
        this.isActive = true;
    }

    // 업체 활성 상태 확인
    public boolean isActiveBusiness() {
        return Boolean.TRUE.equals(this.isActive);
    }
}