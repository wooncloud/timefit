package timefit.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.SlotType;

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

        @NotNull(message = "서비스 타입은 필수입니다")
        private final SlotType slotType;

        private final String description;
        private final String imageUrl;

        private CreateMenu(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                SlotType slotType,
                String description,
                String imageUrl) {

            this.serviceName = serviceName;
            this.category = category;
            this.price = price;
            this.slotType = slotType;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public static CreateMenu of(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                SlotType slotType,
                String description,
                String imageUrl) {

            return new CreateMenu(serviceName, category, price, slotType, description, imageUrl);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            CreateMenu that = (CreateMenu) other;

            return Objects.equals(serviceName, that.serviceName) &&
                    Objects.equals(category, that.category) &&
                    Objects.equals(price, that.price) &&
                    Objects.equals(slotType, that.slotType) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, category, price, slotType, description, imageUrl);
        }
    }

    // 메뉴 수정 요청 (부분 수정 지원)
    @Getter
    public static class UpdateMenu {

        @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
        private final String serviceName;

        private final BusinessTypeCode category;

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
        private final Integer price;

        private final String description;
        private final String imageUrl;

        private UpdateMenu(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                String description,
                String imageUrl) {

            this.serviceName = serviceName;
            this.category = category;
            this.price = price;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public static UpdateMenu of(
                String serviceName,
                BusinessTypeCode category,
                Integer price,
                String description,
                String imageUrl) {

            return new UpdateMenu(serviceName, category, price, description, imageUrl);
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
                    Objects.equals(imageUrl, that.imageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, category, price, description, imageUrl);
        }
    }
}