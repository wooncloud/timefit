package timefit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.dto.response.CurrentUserResponse;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;

    /**
     * 현재 로그인한 사용자의 전체 정보 조회
     * - 사용자 기본 정보
     * - 소속 업체 목록 (활성 상태만)
     *
     * @param userId 사용자 ID
     * @return 사용자 정보 + 업체 목록
     */
    public CurrentUserResponse getCurrentUserInfo(UUID userId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(
                        AuthErrorCode.USER_NOT_FOUND));

        // 2. 소속 업체 목록 조회 (EntityGraph로 N+1 방지)
        List<UserBusinessRole> roles = userBusinessRoleRepository
                .findByUserIdAndIsActive(userId, true);

        // 3. BusinessInfo DTO 변환
        List<CurrentUserResponse.BusinessInfo> businesses = roles.stream()
                .map(role -> CurrentUserResponse.BusinessInfo.from(
                        role.getBusiness(),
                        role.getRole().name()
                ))
                .toList();

        log.info("사용자 정보 조회 완료: userId={}, businessCount={}",
                userId, businesses.size());

        return CurrentUserResponse.of(user, businesses);
    }
}