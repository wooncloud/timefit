package timefit.auth.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;
/**
 * Auth 도메인 검증 클래스
 * 목적:
 * - Auth 고유의 검증 로직 분리
 * - Service 계층 코드 간결화
 * - 검증 로직 재사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 중복 검증
     *
     * @param email 검증할 이메일
     * @throws AuthException 이메일이 이미 존재할 경우
     */
    public void validateEmailNotDuplicated(String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("이미 존재하는 이메일: {}", email);
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 사용자 인증 정보 검증 (로그인)
     *
     * @param email 이메일
     * @param password 비밀번호
     * @return 검증된 User 엔티티
     * @throws AuthException 사용자가 존재하지 않거나 비밀번호가 일치하지 않을 경우
     */
    public User validateUserCredentials(String email, String password) {
        // 1. 사용자 존재 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new AuthException(AuthErrorCode.USER_NOT_FOUND);
                });

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("비밀번호 불일치: {}", email);
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    /**
     * OAuth 사용자 조회 (존재 확인용)
     *
     * @param provider OAuth 프로바이더
     * @param oauthId OAuth ID
     * @return User 엔티티 (Optional)
     */
    public User findOAuthUser(String provider, String oauthId) {
        return userRepository.findByOauthProviderAndOauthId(provider, oauthId)
                .orElse(null);
    }

    /**
     * 사용자 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @return User 엔티티
     * @throws AuthException 사용자가 존재하지 않을 경우
     */
    public User validateUserExists(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자 ID: {}", userId);
                    return new AuthException(AuthErrorCode.USER_NOT_FOUND);
                });
    }

    /**
     * 사용자의 활성화된 비즈니스 권한 조회
     * @param userId 사용자 ID
     * @return 활성화된 비즈니스 권한 목록
     */
    public List<UserBusinessRole> getUserBusinessRoles(UUID userId) {
        log.debug("사용자 비즈니스 권한 조회: userId={}", userId);
        return userBusinessRoleRepository.findByUserIdAndIsActive(userId, true);
    }
}