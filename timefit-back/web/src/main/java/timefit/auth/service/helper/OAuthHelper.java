package timefit.auth.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.auth.service.validator.AuthValidator;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

/**
 * OAuth 사용자 처리 헬퍼
 * 역할:
 * - OAuth 사용자 찾기 또는 생성
 * - OAuth 로그인 흐름 지원
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthHelper {

    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    /**
     * OAuth 사용자 조회 또는 생성
     *
     * @param request OAuth 요청 DTO
     * @param oauthUserInfo OAuth 사용자 정보
     * @return User 엔티티 (기존 또는 신규)
     */
    public User findOrCreateOAuthUser(
            AuthRequestDto.CustomerOAuth request,
            OAuthUserInfo oauthUserInfo) {

        User existingUser = authValidator.findOAuthUser(
                request.provider(),
                request.oauthId()
        );

        if (existingUser != null) {
            log.debug("기존 OAuth 사용자 로그인: userId={}", existingUser.getId());
            existingUser.updateLastLogin();
            return userRepository.save(existingUser);
        } else {
            log.info("신규 OAuth 사용자 생성: provider={}, email={}",
                    request.provider(), oauthUserInfo.email());
            User newUser = User.createOAuthUser(
                    oauthUserInfo.email(),
                    oauthUserInfo.name(),
                    oauthUserInfo.profileImageUrl(),
                    request.provider(),
                    request.oauthId()
            );
            return userRepository.save(newUser);
        }
    }

    /**
     * Mock 사용자 정보 생성 (개발용)
     *
     * 용도:
     * - OAuth API 실제 구현 전 로컬 테스트
     * - 통합 테스트에서 외부 API 의존성 제거
     *
     * 삭제 시점:
     * - OAuthValidator에서 실제 Google/Kakao API 구현 완료 후
     *
     * @param provider OAuth 제공자 (google, kakao)
     * @return 임시 사용자 정보
     */
    public OAuthUserInfo createMockUserInfo(String provider) {
        log.warn("⚠️ [개발용] Mock OAuth 사용자 정보 생성: provider={}", provider);
        log.warn("⚠️ 프로덕션 환경에서는 이 메서드가 호출되지 않아야 합니다!");

        return OAuthUserInfo.of(
                "customer@" + provider + ".com",
                "OAuth 쓰시는 김고객",
                "https://example.com/profile.jpg"
        );
    }
}