package timefit.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.menu.entity.OrderType;

import java.util.Objects;

/**
 * Menu Request DTO
 * - category (BusinessTypeCode) 제거
 * - businessType (대분류) 추가
 * - categoryCode (중분류) 추가
 */
public class MenuRequest {

    /**
     * 메뉴 생성 요청
     * - businessType: Business.businessTypes 중 선택
     * - categoryCode: businessType에 속하는 중분류 선택
     */
    @Getter
    public static class CreateMenu {

        @NotBlank(message = "서비스명은 필수입니다")
        @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
        private final String serviceName;

        /**
         * 대분류 (업종)
         * - Business.businessTypes 중 선택
         */
        @NotNull(message = "업종은 필수입니다")
        private final BusinessTypeCode businessType;

        /**
         * 중분류 (서비스 카테고리)
         * - businessType에 속하는 ServiceCategoryCode 선택
         */
        @NotNull(message = "카테고리는 필수입니다")
        private final ServiceCategoryCode categoryCode;

        @NotNull(message = "가격은 필수입니다")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
        private final Integer price;

        @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
        private final String description;

        @NotNull(message = "주문 타입은 필수입니다")
        private final OrderType orderType;

        @NotNull(message = "소요 시간은 필수입니다")
        @Min(value = 1, message = "소요 시간은 최소 1분 이상이어야 합니다")
        private final Integer durationMinutes;

        private final String imageUrl;

        public CreateMenu(
                String serviceName,
                BusinessTypeCode businessType,
                ServiceCategoryCode categoryCode,
                Integer price,
                String description,
                OrderType orderType,
                Integer durationMinutes,
                String imageUrl) {

            this.serviceName = serviceName;
            this.businessType = businessType;
            this.categoryCode = categoryCode;
            this.price = price;
            this.description = description;
            this.orderType = orderType;
            this.durationMinutes = durationMinutes;
            this.imageUrl = imageUrl;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            CreateMenu that = (CreateMenu) other;
            return Objects.equals(serviceName, that.serviceName) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(categoryCode, that.categoryCode) &&
                    Objects.equals(price, that.price) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(orderType, that.orderType) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, businessType, categoryCode, price,
                    description, orderType, durationMinutes, imageUrl);
        }
    }

    // 메뉴 수정 요청 (모든 필드 nullable for partial update)
    // businessType, categoryCode 추가 (선택적 수정 가능)
    @Getter
    public static class UpdateMenu {

        @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
        private final String serviceName;
        private final BusinessTypeCode businessType;
        private final ServiceCategoryCode categoryCode;

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
        private final Integer price;

        @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
        private final String description;

        @Min(value = 1, message = "소요 시간은 최소 1분 이상이어야 합니다")
        private final Integer durationMinutes;

        private final String imageUrl;

        public UpdateMenu(
                String serviceName,
                BusinessTypeCode businessType,
                ServiceCategoryCode categoryCode,
                Integer price,
                String description,
                Integer durationMinutes,
                String imageUrl) {

            this.serviceName = serviceName;
            this.businessType = businessType;
            this.categoryCode = categoryCode;
            this.price = price;
            this.description = description;
            this.durationMinutes = durationMinutes;
            this.imageUrl = imageUrl;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            UpdateMenu that = (UpdateMenu) other;
            return Objects.equals(serviceName, that.serviceName) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(categoryCode, that.categoryCode) &&
                    Objects.equals(price, that.price) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, businessType, categoryCode, price,
                    description, durationMinutes, imageUrl);
        }
    }
}