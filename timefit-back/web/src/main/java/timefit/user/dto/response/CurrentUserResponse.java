//package timefit.user.dto.response;
//
//import timefit.business.entity.Business;
//import timefit.user.entity.User;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//
///**
// * 현재 로그인한 사용자의 전체 정보
// * - 사업자: businesses 배열 포함
// * - 일반 고객: businesses 빈 배열
// */
//public record CurrentUserResponse(
//        // 사용자 기본 정보
//        UUID userId,
//        String email,
//        String name,
//        String phoneNumber,
//        String profileImageUrl,
//        LocalDateTime createdAt,
//        LocalDateTime lastLoginAt,
//        // 소속 업체 목록 (사업자인 경우)
//        List<BusinessInfo> businesses) {
//
//    // User 엔티티 + 업체 목록으로 DTO 생성
//    public static CurrentUserResponse of(User user, List<BusinessInfo> businesses) {
//        return new CurrentUserResponse(
//                user.getId(),
//                user.getEmail(),
//                user.getName(),
//                user.getPhoneNumber(),
//                user.getProfileImageUrl(),
//                user.getCreatedAt(),
//                user.getLastLoginAt(),
//                businesses != null ? businesses : List.of()
//        );
//    }
//
//    // 업체 정보 (ex: Navbar)
//    public record BusinessInfo(
//            UUID businessId,
//            String businessName,
//            String logoUrl,
//            String myRole,
//            Boolean isActive) {
//
//        // Business 엔티티 + 역할로 DTO 생성
//        public static BusinessInfo from(Business business, String role) {
//            return new BusinessInfo(
//                    business.getId(),
//                    business.getBusinessName(),
//                    business.getLogoUrl(),
//                    role,
//                    business.getIsActive()
//            );
//        }
//    }
//}