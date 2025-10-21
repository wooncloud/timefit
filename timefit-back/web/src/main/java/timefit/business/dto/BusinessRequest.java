package timefit.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import timefit.business.entity.BusinessTypeCode;
import timefit.common.entity.BusinessRole;

import java.util.Set;
import java.util.UUID;

/**
 * Business Request DTO
 * - 업체 생성/수정/삭제/구성원 관리 요청
 */
public class BusinessRequest {

    /**
     * 업체 생성 요청
     */
    @Getter
    @Builder
    public static class CreateBusiness {
        @NotBlank(message = "업체명은 필수입니다")
        private String businessName;

        @NotNull(message = "업종은 필수입니다")
        private Set<BusinessTypeCode> businessTypes;

        @NotBlank(message = "사업자번호는 필수입니다")
        private String businessNumber;

        @NotBlank(message = "주소는 필수입니다")
        private String address;

        @NotBlank(message = "연락처는 필수입니다")
        private String contactPhone;

        private String description;
    }

    /**
     * 업체 정보 수정 요청
     */
    @Getter
    @Builder
    public static class UpdateBusiness {
        private String businessName;
        private Set<BusinessTypeCode> businessTypes;
        private String businessNumber;
        private String address;
        private String contactPhone;
        private String description;
        private String logoUrl;
    }

    /**
     * 업체 삭제 요청
     */
    @Getter
    @Builder
    public static class DeleteBusiness {
        @NotNull(message = "삭제 확인은 필수입니다")
        private Boolean confirmDelete;
    }

    /**
     * 구성원 초대 요청
     */
    @Getter
    @Builder
    public static class InviteUser {
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotNull(message = "권한은 필수입니다")
        private BusinessRole role;

        private String invitationMessage;
    }

    /**
     * 구성원 권한 변경 요청
     */
    @Getter
    @Builder
    public static class ChangeRole {
        @NotNull(message = "변경할 권한은 필수입니다")
        private BusinessRole newRole;
    }
}