package timefit.service.entity;

import timefit.business.entity.Business;
import timefit.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Service extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @NotBlank(message = "서비스명은 필수입니다")
    @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Size(max = 50, message = "카테고리는 50자 이하로 입력해주세요")
    @Column(length = 50)
    private String category;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Column(nullable = false)
    private Integer price;

    @NotNull(message = "소요시간은 필수입니다")
    @Min(value = 1, message = "소요시간은 1분 이상이어야 합니다")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}