package timefit.business.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import timefit.common.entity.BaseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "business",
        indexes = {
                @Index(name = "idx_business_rating", columnList = "average_rating DESC"),
                @Index(name = "idx_business_review_count", columnList = "review_count DESC")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseEntity {

    @NotBlank(message = "상호명은 필수입니다")
    @Size(min = 2, max = 100, message = "상호명은 2자 이상 100자 이하로 입력해주세요")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    /**
     * - 업체가 제공하는 업종(대분류) 목록
     * - UX: 최초 Business 생성 시 선택
     * - 초기: 1개 (예: HAIR)
     * - 확장: 여러 개 추가 가능 (예: HAIR, NAIL)
     */
    @ElementCollection(targetClass = BusinessTypeCode.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "business_type",
            joinColumns = @JoinColumn(name = "business_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "type_code", nullable = false, length = 10)
    @NotEmpty(message = "최소 1개 이상의 업종을 선택해야 합니다")
    private Set<BusinessTypeCode> businessTypes = new HashSet<>();

    @NotBlank(message = "사업자번호는 필수입니다")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
    @Column(name = "business_number", nullable = false)
    private String businessNumber;

    @Size(max = 50, message = "대표자명은 50자 이하로 입력해주세요")
    @Column(name = "owner_name", length = 50)
    private String ownerName;

    @Size(max = 200, message = "주소는 200자 이하로 입력해주세요")
    private String address;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "business_notice", columnDefinition = "TEXT")
    private String businessNotice;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;


    // ----------------- 정적 팩토리 메서드

    // 업체 새로 생성
    public static Business createBusiness(
            String businessName,
            Set<BusinessTypeCode> businessTypes,
            String businessNumber,
            String ownerName,
            String address,
            String contactPhone,
            String description,
            String logoUrl,
            String businessNotice
    ) {
        Business business = new Business();
        business.businessName = businessName;
        business.businessTypes = businessTypes;
        business.businessNumber = businessNumber;
        business.ownerName = ownerName;
        business.address = address;
        business.contactPhone = contactPhone;
        business.description = description;
        business.logoUrl = logoUrl;
        business.businessNotice = businessNotice;
        business.isActive = true;
        return business;
    }

    // -------------------------- 비즈니스 메서드

    // 업체 전체 정보 업데이트
    public void updateBusinessInfo(
            String businessName,
            Set<BusinessTypeCode> businessTypes,
            String ownerName,
            String address,
            String contactPhone,
            String description,
            String logoUrl,
            String businessNotice) {

        if (businessName != null) {
            this.businessName = businessName;
        }
        if (businessTypes != null) {
            this.businessTypes = businessTypes;
        }
        if (ownerName != null) {
            this.ownerName = ownerName;
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
        if (businessNotice != null) {
            this.businessNotice = businessNotice;
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

    public boolean hasBusinessType(BusinessTypeCode businessType) {
        return this.businessTypes != null && this.businessTypes.contains(businessType);
    }

    public boolean isActive() {
        return this.isActive;
    }

    /**
     * 평점 및 리뷰 수 업데이트
     * 리뷰 작성/수정/삭제 시 호출
     * @param newAverageRating 새 평균 평점
     * @param newReviewCount 새 리뷰 개수
     */
    public void updateRating(Double newAverageRating, Integer newReviewCount) {
        if (newAverageRating == null || newAverageRating < 0.0 || newAverageRating > 5.0) {
            throw new IllegalArgumentException("평균 평점은 0.0~5.0 사이여야 합니다");
        }
        if (newReviewCount == null || newReviewCount < 0) {
            throw new IllegalArgumentException("리뷰 개수는 0 이상이어야 합니다");
        }

        this.averageRating = newAverageRating;
        this.reviewCount = newReviewCount;
    }

    /**
     * 위치 정보 업데이트
     * @param latitude 위도
     * @param longitude 경도
     */
    public void updateLocation(Double latitude, Double longitude) {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new IllegalArgumentException("위도는 -90.0~90.0 사이여야 합니다");
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new IllegalArgumentException("경도는 -180.0~180.0 사이여야 합니다");
        }

        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * 평점이 있는지 확인
     * @return 리뷰가 1개 이상 있으면 true
     */
    public boolean hasReviews() {
        return this.reviewCount != null && this.reviewCount > 0;
    }
}