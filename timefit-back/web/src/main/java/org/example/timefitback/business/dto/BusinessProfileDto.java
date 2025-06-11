package org.example.timefitback.business.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class BusinessProfileDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResponse {
        private UUID businessId;
        private String businessName;
        private String businessType;
        private String businessNumber;
        private String address;
        private String contactPhone;
        private String description;
        private String logoUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 사용자 기본 정보
        private UUID userId;
        private String userEmail;
        private String userName;
        private String userPhoneNumber;
        private String profileImageUrl;

        // 정적 팩토리 메서드
        public static ProfileResponse of(UUID businessId, String businessName, String businessType,
                                            String businessNumber, String address, String contactPhone,
                                            String description, String logoUrl, LocalDateTime createdAt,
                                            LocalDateTime updatedAt, UUID userId, String userEmail,
                                            String userName, String userPhoneNumber, String profileImageUrl) {
            return new ProfileResponse(businessId, businessName, businessType, businessNumber,
                    address, contactPhone, description, logoUrl, createdAt, updatedAt,
                    userId, userEmail, userName, userPhoneNumber, profileImageUrl);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {

        @NotBlank(message = "상호명은 필수입니다")
        @Size(min = 2, max = 100, message = "상호명은 2자 이상 100자 이하로 입력해주세요")
        private String businessName;

        private String businessType;

        @Size(max = 200, message = "주소는 200자 이하로 입력해주세요")
        private String address;

        @Pattern(regexp = "^0[2-9][0-9]?-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
        private String contactPhone;

        @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
        private String description;

        private String logoUrl;

        // 사용자 정보
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
        private String userName;

        @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
        private String userPhoneNumber;

        private String profileImageUrl;

        // 전화번호 정규화
        public void normalizePhoneNumber() {
            if (contactPhone != null) {
                this.contactPhone = contactPhone.replaceAll("-", "");
            }
            if (userPhoneNumber != null) {
                this.userPhoneNumber = userPhoneNumber.replaceAll("-", "");
            }
        }
    }
}