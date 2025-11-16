package timefit.business.dto;

import jakarta.validation.constraints.*;
import timefit.common.entity.BusinessRole;
import timefit.business.entity.BusinessTypeCode;

import java.util.Set;

public class BusinessRequestDto {

    // 업체 생성 요청 (필수조건 NotBlank 로 blocking 처리)
    public record CreateBusinessRequest(
            @NotBlank(message = "업체명은 필수입니다")
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            String businessName,

            @NotNull(message = "업종은 필수입니다")
            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            Set<BusinessTypeCode> businessTypes,

            @NotBlank(message = "사업자번호는 필수입니다")
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            String businessNumber,

            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            String ownerName,

            @NotBlank(message = "주소는 필수입니다")
            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            String address,

            @NotBlank(message = "연락처는 필수입니다")
            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            String contactPhone,

            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            String description,

            String logoUrl,

            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            String businessNotice
    ) {}

    // 업체 정보 수정 요청
    public record UpdateBusinessRequest(
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            String businessName,

            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            Set<BusinessTypeCode> businessTypes,

            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            String businessNumber,

            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            String ownerName,

            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            String address,

            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            String contactPhone,

            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            String description,

            String logoUrl,

            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            String businessNotice
    ) {}

    // 멤버 초대 요청
    public record InviteMemberRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @Size(max = 500, message = "초대 메시지는 500자 이하여야 합니다")
            String invitationMessage
    ) {}

    // 멤버 권한 변경 요청
    public record ChangeMemberRoleRequest(
            @NotNull(message = "새로운 역할은 필수입니다")
            BusinessRole newRole
    ) {}

    // 업체 삭제 요청
    public record DeleteBusinessRequest(
            @NotBlank(message = "삭제 사유는 필수입니다")
            @Size(max = 500, message = "삭제 사유는 500자 이하여야 합니다")
            String deleteReason
    ) {}
}