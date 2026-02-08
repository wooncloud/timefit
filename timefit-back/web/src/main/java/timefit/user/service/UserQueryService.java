package timefit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.validator.AuthValidator;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.reservation.repository.ReservationRepository;
import timefit.review.service.ReviewService;
import timefit.user.dto.UserResponseDto;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;
import timefit.wishlist.service.WishlistService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final AuthValidator authValidator;
    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final ReservationRepository reservationRepository;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;

    /**
     * 현재 로그인한 사용자의 전체 정보 조회 (/api/user/me)
     * - 사용자 기본 정보
     * - 소속 업체 목록 (활성 상태만)
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 + 업체 목록
     */
    public UserResponseDto.CurrentUser getCurrentUserInfo(UUID userId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND));

        // 2. 소속 업체 목록 조회 (EntityGraph로 N+1 방지)
        List<UserBusinessRole> roles = userBusinessRoleRepository
                .findByUserIdAndIsActive(userId, true);

        // 3. BusinessInfo DTO 변환
        List<UserResponseDto.BusinessInfo> businesses = roles.stream()
                .map(role -> UserResponseDto.BusinessInfo.from(
                        role.getBusiness(),
                        role.getRole().name()
                ))
                .toList();

        log.info("사용자 정보 조회 완료: userId={}, businessCount={}",
                userId, businesses.size());

        return UserResponseDto.CurrentUser.of(user, businesses);
    }

    /**
     * 사용자 프로필 조회 (통계 포함) (/api/customer/profile)
     *
     * 프로세스:
     * 1. 사용자 검증
     * 2. 통계 계산 (예약/찜/리뷰 수)
     * 3. DTO 변환
     *
     * @param userId 사용자 ID
     * @return 사용자 프로필 + 통계
     */
    public UserResponseDto.UserProfile getUserProfile(UUID userId) {
        log.info("사용자 프로필 조회 시작: userId={}", userId);

        // 1. 사용자 검증
        User user = authValidator.validateUserExists(userId);

        // 2. 통계 계산
        UserResponseDto.UserStatistics statistics = getUserStatistics(userId);

        log.info("사용자 프로필 조회 완료: userId={}, email={}", userId, user.getEmail());

        // 3. DTO 변환
        return UserResponseDto.UserProfile.of(user, statistics);
    }

    /**
     * 사용자 통계 계산
     *
     * 프로세스:
     * 1. 전체 예약 수 조회
     * 2. 찜 개수 조회
     * 3. 리뷰 개수 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 통계
     */
    public UserResponseDto.UserStatistics getUserStatistics(UUID userId) {
        log.debug("사용자 통계 계산: userId={}", userId);

        // 1. 전체 예약 수 (삭제되지 않은 예약만)
        long totalReservations = reservationRepository.countByCustomerId(userId);

        // 2. 찜 개수
        long wishlistCount = wishlistService.getWishlistCount(userId);

        // 3. 리뷰 개수 (삭제되지 않은 리뷰만)
        long reviewCount = reviewService.getUserReviewCount(userId);

        log.debug("사용자 통계 계산 완료: userId={}, reservations={}, wishlists={}, reviews={}",
                userId, totalReservations, wishlistCount, reviewCount);

        return UserResponseDto.UserStatistics.of(totalReservations, wishlistCount, reviewCount);
    }
}