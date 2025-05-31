package org.example.timefitback.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "business")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 낙관적 락을 위한 버전 필드
    @Version
    @Column(nullable = false)
    private Integer version = 0;


    public static Business createBusiness(UUID userId, String businessName, String businessType,
                                            String businessNumber, String address, String contactPhone, String description) {
        Business business = new Business();
        business.userId = userId;
        business.businessName = businessName;
        business.businessType = businessType;
        business.businessNumber = businessNumber;
        business.address = address;
        business.contactPhone = contactPhone;
        business.description = description;
        return business;
    }

    public void updateBusinessInfo(String businessName, String businessType, String address,
                                    String contactPhone, String description, String logoUrl) {
        if (businessName != null && !businessName.trim().isEmpty()) {
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

    // 사업자번호 검증 (정규식 체크)
    public boolean isValidBusinessNumber() {
        if (businessNumber == null) return false;
        return businessNumber.matches("^[0-9]{3}-[0-9]{2}-[0-9]{5}$");
    }

    // 필수 정보 완성도 체크
    public boolean isBasicInfoComplete() {
        return businessName != null && !businessName.trim().isEmpty() &&
                businessNumber != null && isValidBusinessNumber();
    }
}