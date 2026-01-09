package timefit.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import timefit.common.entity.BusinessRole;
import timefit.business.entity.BusinessTypeCode;

import java.util.Set;

@Schema(description = "업체 관리 요청")
public class BusinessRequestDto {

    @Schema(description = "업체 생성 요청")
    public record CreateBusinessRequest(
            @Schema(
                    description = "업체명",
                    example = "Owner Kim 미용실",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    minLength = 2,
                    maxLength = 100
            )
            @NotBlank(message = "업체명은 필수입니다")
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            String businessName,

            @Schema(
                    description = "업종 코드 목록 (최소 1개, 최대 3개)",
                    example = "[\"BD008\"]",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            @NotNull(message = "업종은 필수입니다")
            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            Set<BusinessTypeCode> businessTypes,

            @Schema(
                    description = "사업자번호 (형식: 000-00-00000)",
                    example = "123-45-67890",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    pattern = "\\d{3}-\\d{2}-\\d{5}"
            )
            @NotBlank(message = "사업자번호는 필수입니다")
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            String businessNumber,

            @Schema(
                    description = "대표자명",
                    example = "Owner Kim",
                    maxLength = 50
            )
            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            String ownerName,

            @Schema(
                    description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 123",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    maxLength = 200
            )
            @NotBlank(message = "주소는 필수입니다")
            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            String address,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    maxLength = 20
            )
            @NotBlank(message = "연락처는 필수입니다")
            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            String contactPhone,

            @Schema(
                    description = "업체 설명",
                    example = "20년 경력의 전문 미용실입니다.",
                    maxLength = 1000
            )
            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            String description,

            @Schema(
                    description = "로고 이미지 URL",
                    example = "https://example.com/logo.png"
            )
            String logoUrl,

            @Schema(
                    description = "업체 공지사항 (내부용)",
                    example = "영업시간 변경: 평일 10:00-20:00",
                    maxLength = 500
            )
            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            String businessNotice
    ) {}

    // 업체 정보 수정 요청
    @Schema(description = "업체 정보 수정 요청 (모든 필드 선택 사항)")
    public record UpdateBusinessRequest(
            @Schema(
                    description = "업체명",
                    example = "Owner Kim 헤어샵",
                    nullable = true,
                    minLength = 2,
                    maxLength = 100
            )
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            String businessName,

            @Schema(
                    description = "업종 코드 목록 (최소 1개, 최대 3개)",
                    example = "[\"BD008\"]",
                    nullable = true,
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            Set<BusinessTypeCode> businessTypes,

            @Schema(
                    description = "사업자번호 (형식: 000-00-00000)",
                    example = "123-45-67890",
                    nullable = true,
                    pattern = "\\d{3}-\\d{2}-\\d{5}"
            )
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            String businessNumber,

            @Schema(
                    description = "대표자명",
                    example = "Owner Kim",
                    nullable = true,
                    maxLength = 50
            )
            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            String ownerName,

            @Schema(
                    description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 456",
                    nullable = true,
                    maxLength = 200
            )
            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            String address,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1111-1111",
                    nullable = true,
                    maxLength = 20
            )
            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            String contactPhone,

            @Schema(
                    description = "업체 설명",
                    example = "30년 경력의 프리미엄 헤어샵입니다.",
                    nullable = true,
                    maxLength = 1000
            )
            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            String description,

            @Schema(
                    description = "로고 이미지 URL",
                    example = "https://example.com/new-logo.png",
                    nullable = true
            )
            String logoUrl,

            @Schema(
                    description = "업체 공지사항 (내부용)",
                    example = "영업시간 변경: 주말 휴무",
                    nullable = true,
                    maxLength = 500
            )
            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            String businessNotice
    ) {}

    // 멤버 초대 요청
    @Schema(description = "구성원 초대 요청 (신규 초대는 무조건 MEMBER로 시작)")
    public record InviteMemberRequest(
            @Schema(
                    description = "초대할 사용자의 이메일",
                    example = "member@example.com",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            String email,

            @Schema(
                    description = "초대 메시지",
                    example = "팀에 합류해주세요!",
                    nullable = true,
                    maxLength = 500
            )
            @Size(max = 500, message = "초대 메시지는 500자 이하여야 합니다")
            String invitationMessage
    ) {}

    // 멤버 권한 변경 요청
    @Schema(description = "구성원 권한 변경 요청 (OWNER 권한으로는 변경 불가)")
    public record ChangeMemberRoleRequest(
            @Schema(
                    description = "변경할 권한 (OWNER로는 변경 불가)",
                    example = "MANAGER",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"MANAGER", "MEMBER"}
            )
            @NotNull(message = "새로운 역할은 필수입니다")
            BusinessRole newRole
    ) {}

    @Schema(description = "업체 삭제 요청")
    public record DeleteBusinessRequest(
            @Schema(
                    description = "삭제 사유",
                    example = "사업 종료",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    maxLength = 500
            )
            @NotBlank(message = "삭제 사유는 필수입니다")
            @Size(max = 500, message = "삭제 사유는 500자 이하여야 합니다")
            String deleteReason
    ) {}
}