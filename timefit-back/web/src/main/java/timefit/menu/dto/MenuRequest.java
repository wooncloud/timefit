package timefit.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.OrderType;

import java.util.Objects;

public class MenuRequest {

    // 메뉴 생성 요청
    @Getter
    public static class CreateMenu {

        @NotBlank(message = "서비스명은 필수입니다")
        @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
        private final String serviceName;

        @NotNull(message = "카테고리는 필수입니다")
        private final BusinessTypeCode category;

        @NotNull(message = "가격은 필수입니다")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
        private final Integer price;

        @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
        private final String description;

        @NotNull(message = "주문 타입은 필수입니다")
        private final OrderType orderType;

        // ✅ 추가: 서비스 소요 시간 (필수)
        @NotNull(message = "소요 시간은 필수입니다")
        @Min(value = 1, message = "소요 시간은 최소 1분 이상이어야 합니다")
        private final Integer durationMinutes;

        private final String imageUrl;

        public CreateMenu(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                String description,
                OrderType orderType,
                Integer durationMinutes,
                String imageUrl) {

            this.serviceName = serviceName;
            this.category = category;
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
                    Objects.equals(category, that.category) &&
                    Objects.equals(price, that.price) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(orderType, that.orderType) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, category, price, description,
                    orderType, durationMinutes, imageUrl);
        }
    }

    // 메뉴 수정 요청 (모든 필드 nullable for partial update)
    @Getter
    public static class UpdateMenu {

        @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
        private final String serviceName;

        private final BusinessTypeCode category;

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
        private final Integer price;

        @Size(max = 500, message = "설명은 500자 이하로 입력해주세요")
        private final String description;

        // ✅ 추가: 소요 시간 수정 (nullable)
        @Min(value = 1, message = "소요 시간은 최소 1분 이상이어야 합니다")
        private final Integer durationMinutes;

        private final String imageUrl;

        public UpdateMenu(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                String description,
                Integer durationMinutes,
                String imageUrl) {

            this.serviceName = serviceName;
            this.category = category;
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
                    Objects.equals(category, that.category) &&
                    Objects.equals(price, that.price) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(durationMinutes, that.durationMinutes) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, category, price, description,
                    durationMinutes, imageUrl);
        }
    }
}