package org.example.timefitback.business.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class BusinessAuthDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpRequest {

        @Email(message = "유효한 이메일 주소를 입력해주세요")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].*$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다")
        private String password;

        @NotBlank(message = "이름을 입력해주세요")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요")
        private String name;

        @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
        private String phoneNumber;

        // Business 정보
        @NotBlank(message = "상호명을 기입해주세요")
        @Size(min = 2, max = 100, message = "상호명은 2자 이상 100자 이하로 입력해주세요")
        private String businessName;

        private String businessType;

        @NotBlank(message = "사업자번호을 기입해주세요")
        @Pattern(regexp = "^[0-9]{3}-[0-9]{2}-[0-9]{5}$", message = "사업자번호 형식이 올바르지 않습니다 (예: 123-45-67890)")
        private String businessNumber;

        @Size(max = 200, message = "주소는 200자 이하로 입력해주세요")
        private String address;

        @Pattern(regexp = "^0[2-9][0-9]?-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
        private String contactPhone;

        @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요")
        private String description;

        // 휴대폰번호 정규화
        public void normalizePhoneNumber() {
            if (phoneNumber != null) {
                this.phoneNumber = phoneNumber.replaceAll("-", "");
            }
            if (contactPhone != null) {
                this.contactPhone = contactPhone.replaceAll("-", "");
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignInRequest {
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        @NotBlank(message = "이메일을 입력해주세요")
        private String email;

        @NotBlank(message = "비밀번호을 입력해주세요")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private UUID userId;
        private String email;
        private String name;
        private String phoneNumber;
        private String role;
        private BusinessInfo business;
        private LocalDateTime lastLoginAt;


        public static AuthResponse of(UUID userId, String email, String name, String phoneNumber,
                                        String role, BusinessInfo business, LocalDateTime lastLoginAt) {
            return new AuthResponse(userId, email, name, phoneNumber, role, business, lastLoginAt);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
        private UUID businessId;
        private String businessName;
        private String businessType;
        private String businessNumber;
        private String address;
        private String contactPhone;
        private String description;
        private String logoUrl;
        private boolean isBasicInfoComplete;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // 정적 팩토리 메서드 - Service 에서 사용
        public static BusinessInfo of(UUID businessId, String businessName, String businessType,
                                        String businessNumber, String address, String contactPhone,
                                        String description, String logoUrl, boolean isBasicInfoComplete,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
            return new BusinessInfo(businessId, businessName, businessType, businessNumber,
                    address, contactPhone, description, logoUrl, isBasicInfoComplete,
                    createdAt, updatedAt);
        }
    }
}