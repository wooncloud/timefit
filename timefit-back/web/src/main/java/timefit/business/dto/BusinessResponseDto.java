package timefit.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;
import timefit.invitation.dto.InvitationResponseDto;
import timefit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Schema(description = "업체 관리 응답")
public class BusinessResponseDto {

    // 사업자용 업체 상세 정보
    @Schema(description = "사업자용 업체 상세 정보 (민감 정보 포함)")
    public record BusinessResponse(
            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "홍길동 미용실"
            )
            String businessName,

            @Schema(
                    description = "업종 코드 목록",
                    example = "[\"BD008\"]"
            )
            Set<BusinessTypeCode> businessTypes,

            @Schema(
                    description = "사업자번호",
                    example = "123-45-67890"
            )
            String businessNumber,

            @Schema(
                    description = "대표자명",
                    example = "홍길동"
            )
            String ownerName,

            @Schema(
                    description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 123"
            )
            String address,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1234-5678"
            )
            String contactPhone,

            @Schema(
                    description = "업체 설명",
                    example = "20년 경력의 전문 미용실입니다."
            )
            String description,

            @Schema(
                    description = "로고 이미지 URL",
                    example = "https://example.com/logo.png"
            )
            String logoUrl,

            @Schema(
                    description = "업체 공지사항 (내부용)",
                    example = "영업시간 변경: 평일 10:00-20:00"
            )
            String businessNotice,

            @Schema(
                    description = "활성화 여부",
                    example = "true"
            )
            Boolean isActive,

            @Schema(
                    description = "현재 사용자의 권한",
                    example = "OWNER",
                    allowableValues = {"OWNER", "MANAGER", "MEMBER"}
            )
            BusinessRole myRole,

            @Schema(
                    description = "생성일시",
                    example = "2025-11-01T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "수정일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt
    ) {
        public static BusinessResponse of(Business business, UserBusinessRole userRole) {
            return new BusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getBusinessNumber(),
                    business.getOwnerName(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    business.getBusinessNotice(),
                    business.getIsActive(),
                    userRole.getRole(),
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }

    // 고객용 업체 공개 정보
    @Schema(description = "공개용 업체 상세 정보 (민감 정보 제외)")
    public record PublicBusinessResponse(
            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "홍길동 미용실"
            )
            String businessName,

            @Schema(
                    description = "업종 코드 목록",
                    example = "[\"BD008\"]"
            )
            Set<BusinessTypeCode> businessTypes,

            @Schema(
                    description = "대표자명",
                    example = "홍길동"
            )
            String ownerName,

            @Schema(
                    description = "업체 주소",
                    example = "서울특별시 강남구 테헤란로 123"
            )
            String address,

            @Schema(
                    description = "업체 연락처",
                    example = "02-1234-5678"
            )
            String contactPhone,

            @Schema(
                    description = "업체 설명",
                    example = "20년 경력의 전문 미용실입니다."
            )
            String description,

            @Schema(
                    description = "로고 이미지 URL",
                    example = "https://example.com/logo.png"
            )
            String logoUrl,

            @Schema(
                    description = "생성일시",
                    example = "2025-11-01T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "수정일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt
    ) {
        public static PublicBusinessResponse of(Business business) {
            return new PublicBusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    business.getBusinessTypes(),
                    business.getOwnerName(),
                    business.getAddress(),
                    business.getContactPhone(),
                    business.getDescription(),
                    business.getLogoUrl(),
                    business.getCreatedAt(),
                    business.getUpdatedAt()
            );
        }
    }

    // 내 업체 목록 응답
    @Schema(description = "업체 목록 응답 (검색, 내 업체 목록)")
    public record BusinessListResponse(
            @Schema(description = "업체 목록")
            List<BusinessItem> businesses,

            @Schema(
                    description = "전체 업체 수",
                    example = "5"
            )
            Integer totalCount
    ) {
        @Schema(description = "업체 목록 아이템")
        public record BusinessItem(
                @Schema(
                        description = "업체 ID",
                        example = "550e8400-e29b-41d4-a716-446655440001"
                )
                UUID businessId,

                @Schema(
                        description = "업체명",
                        example = "홍길동 미용실"
                )
                String businessName,

                @Schema(
                        description = "업종 코드 목록",
                        example = "[\"BD008\"]"
                )
                Set<BusinessTypeCode> businessTypes,

                @Schema(
                        description = "로고 이미지 URL",
                        example = "https://example.com/logo.png"
                )
                String logoUrl,

                @Schema(
                        description = "내 권한 (내 업체 목록에서만 제공, 검색에서는 null)",
                        example = "MANAGER",
                        nullable = true,
                        allowableValues = {"OWNER", "MANAGER", "MEMBER"}
                )
                BusinessRole myRole,

                @Schema(
                        description = "가입일시 (내 업체 목록에서만 제공)",
                        example = "2025-11-01T10:00:00",
                        nullable = true
                )
                LocalDateTime joinedAt,

                @Schema(
                        description = "활성화 여부",
                        example = "true"
                )
                Boolean isActive
        ) {
            public static BusinessItem of(Business business, UserBusinessRole userRole) {
                return new BusinessItem(
                        business.getId(),
                        business.getBusinessName(),
                        business.getBusinessTypes(),
                        business.getLogoUrl(),
                        userRole.getRole(),
                        userRole.getJoinedAt(),
                        userRole.getIsActive()
                );
            }
        }

        public static BusinessListResponse of(List<UserBusinessRole> userRoles) {
            List<BusinessItem> items = userRoles.stream()
                    .map(userRole -> BusinessItem.of(userRole.getBusiness(), userRole))
                    .toList();

            return new BusinessListResponse(items, items.size());
        }
    }

    // 팀원 목록 응답
    @Schema(description = "업체 구성원 목록 응답")
    public record MemberListResponse(
            @Schema(
                    description = "업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "홍길동 미용실"
            )
            String businessName,

            @Schema(description = "구성원 목록")
            List<MemberResponse> members,

            @Schema(
                    description = "전체 구성원 수",
                    example = "5"
            )
            Integer totalCount
    ) {
        @Schema(description = "구성원 정보")
        public record MemberResponse(
                @Schema(
                        description = "사용자 ID",
                        example = "660e8400-e29b-41d4-a716-446655440002"
                )
                UUID userId,

                @Schema(
                        description = "이메일",
                        example = "member@example.com"
                )
                String email,

                @Schema(
                        description = "사용자 이름",
                        example = "김직원"
                )
                String name,

                @Schema(
                        description = "권한",
                        example = "MANAGER",
                        allowableValues = {"OWNER", "MANAGER", "MEMBER"}
                )
                BusinessRole role,

                @Schema(
                        description = "가입일시",
                        example = "2025-11-01T10:00:00"
                )
                LocalDateTime joinedAt,

                @Schema(
                        description = "활성화 여부",
                        example = "true"
                )
                Boolean isActive,

                @Schema(
                        description = "초대자 이름",
                        example = "홍길동",
                        nullable = true
                )
                String invitedByName,

                @Schema(
                        description = "마지막 로그인 시각",
                        example = "2025-11-23T15:30:00",
                        nullable = true
                )
                LocalDateTime lastLoginAt
        ) {
            public static MemberResponse of(UserBusinessRole userRole) {
                User user = userRole.getUser();
                User invitedBy = userRole.getInvitedBy();

                return new MemberResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        userRole.getRole(),
                        userRole.getJoinedAt(),
                        userRole.getIsActive(),
                        invitedBy != null ? invitedBy.getName() : null,
                        user.getLastLoginAt()
                );
            }

            public static MemberResponse fromInvitation(InvitationResponseDto.Invitation invitation) {
                return new MemberResponse(
                        null,  // userId: 초대 수락 전이므로 null
                        invitation.email(),
                        null,  // name: 초대 수락 전이므로 null
                        invitation.role(),
                        null,  // joinedAt: 초대 수락 전이므로 null
                        null,  // isActive: 초대 PENDING 상태
                        invitation.invitedByName(),
                        null   // lastLoginAt: 초대 수락 전이므로 null
                );
            }
        }

        public static MemberListResponse of(Business business, List<UserBusinessRole> userRoles) {
            List<MemberResponse> members = userRoles.stream()
                    .map(MemberResponse::of)
                    .toList();

            return new MemberListResponse(
                    business.getId(),
                    business.getBusinessName(),
                    members,
                    members.size()
            );
        }
    }

    // 업체 삭제 결과
    @Schema(description = "업체 삭제(비활성화) 결과")
    public record DeleteBusinessResponse(
            @Schema(
                    description = "삭제된 업체 ID",
                    example = "550e8400-e29b-41d4-a716-446655440001"
            )
            UUID businessId,

            @Schema(
                    description = "업체명",
                    example = "홍길동 미용실"
            )
            String businessName,

            @Schema(
                    description = "삭제 처리 시각",
                    example = "2025-11-23T16:00:00"
            )
            LocalDateTime deletedAt,

            @Schema(
                    description = "삭제 사유",
                    example = "사업 종료"
            )
            String deleteReason
    ) {
        public static DeleteBusinessResponse of(Business business, String deleteReason) {
            return new DeleteBusinessResponse(
                    business.getId(),
                    business.getBusinessName(),
                    LocalDateTime.now(),
                    deleteReason
            );
        }
    }
}