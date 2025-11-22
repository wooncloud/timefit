package timefit.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import timefit.common.entity.BusinessRole;
import timefit.business.entity.BusinessTypeCode;

import java.util.Set;

@Schema(description = "업체 관리 Request DTO 모음")
public class BusinessRequestDto {

    // 업체 생성 요청 (필수조건 NotBlank 로 blocking 처리)
    @Schema(description = "업체 생성 요청")
    public record CreateBusinessRequest(
            @NotBlank(message = "업체명은 필수입니다")
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            @Schema(description = "업체명", example = "홍길동 미용실", requiredMode = Schema.RequiredMode.REQUIRED)
            String businessName,

            @NotNull(message = "업종은 필수입니다")
            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            @Schema(description = "업종 코드 목록 (최소 1개, 최대 3개)",
                    example = "[\"BD003\"]",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            Set<BusinessTypeCode> businessTypes,

            @NotBlank(message = "사업자번호는 필수입니다")
            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            @Schema(description = "사업자번호 (형식: 000-00-00000)",
                    example = "123-45-67890",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String businessNumber,

            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            @Schema(description = "대표자명", example = "홍길동")
            String ownerName,

            @NotBlank(message = "주소는 필수입니다")
            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            @Schema(description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 123",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String address,

            @NotBlank(message = "연락처는 필수입니다")
            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            @Schema(description = "업체 연락처",
                    example = "02-1234-5678",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String contactPhone,

            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            @Schema(description = "업체 설명", example = "20년 경력의 전문 미용실입니다.")
            String description,

            @Schema(description = "로고 이미지 URL", example = "https://example.com/logo.png")
            String logoUrl,

            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            @Schema(description = "업체 공지사항 (내부용)", example = "영업시간 변경: 평일 10:00-20:00")
            String businessNotice
    ) {}

    // 업체 정보 수정 요청
    @Schema(description = "업체 정보 수정 요청 (모든 필드 선택 사항)")
    public record UpdateBusinessRequest(
            @Size(min = 2, max = 100, message = "업체명은 2자 이상 100자 이하여야 합니다")
            @Schema(description = "업체명", example = "홍길동 미용실", nullable = true)
            String businessName,

            @Size(min = 1, max = 3, message = "업종은 최소 1개, 최대 3개까지 선택 가능합니다")
            @Schema(description = "업종 코드 목록 (최소 1개, 최대 3개)",
                    example = "[\"BD003\"]",
                    nullable = true)
            Set<BusinessTypeCode> businessTypes,

            @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자번호 형식이 올바르지 않습니다")
            @Schema(description = "사업자번호 (형식: 000-00-00000)",
                    example = "123-45-67890",
                    nullable = true)
            String businessNumber,

            @Size(max = 50, message = "대표자명은 50자 이하여야 합니다")
            @Schema(description = "대표자명", example = "홍길동", nullable = true)
            String ownerName,

            @Size(max = 200, message = "주소는 200자 이하여야 합니다")
            @Schema(description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 123",
                    nullable = true)
            String address,

            @Size(max = 20, message = "연락처는 20자 이하여야 합니다")
            @Schema(description = "업체 연락처", example = "02-1234-5678", nullable = true)
            String contactPhone,

            @Size(max = 1000, message = "설명은 1000자 이하여야 합니다")
            @Schema(description = "업체 설명",
                    example = "20년 경력의 전문 미용실입니다.",
                    nullable = true)
            String description,

            @Schema(description = "로고 이미지 URL", example = "https://example.com/logo.png", nullable = true)
            String logoUrl,

            @Size(max = 500, message = "공지사항은 500자 이하여야 합니다")
            @Schema(description = "업체 공지사항 (내부용)",
                    example = "영업시간 변경: 평일 10:00-20:00",
                    nullable = true)
            String businessNotice
    ) {}

    // 멤버 초대 요청
    @Schema(description = "구성원 초대 요청 (신규 초대는 무조건 MEMBER로 시작)")
    public record InviteMemberRequest(
            @NotBlank(message = "이메일은 필수입니다")
            @Email(message = "올바른 이메일 형식이 아닙니다")
            @Schema(description = "초대할 사용자의 이메일",
                    example = "member@example.com",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String email,

            @Size(max = 500, message = "초대 메시지는 500자 이하여야 합니다")
            @Schema(description = "초대 메시지", example = "팀에 합류해주세요!", nullable = true)
            String invitationMessage
    ) {}

    // 멤버 권한 변경 요청
    @Schema(description = "구성원 권한 변경 요청 (OWNER 권한으로는 변경 불가)")
    public record ChangeMemberRoleRequest(
            @NotNull(message = "새로운 역할은 필수입니다")
            @Schema(description = "변경할 권한 (OWNER로는 변경 불가)",
                    example = "MANAGER",
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"MANAGER", "MEMBER"})
            BusinessRole newRole
    ) {}

    // 업체 삭제 요청
    @Schema(description = "업체 삭제(비활성화) 요청")
    public record DeleteBusinessRequest(
            @NotBlank(message = "삭제 사유는 필수입니다")
            @Size(max = 500, message = "삭제 사유는 500자 이하여야 합니다")
            @Schema(description = "삭제 사유",
                    example = "사업 종료",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String deleteReason
    ) {}
}