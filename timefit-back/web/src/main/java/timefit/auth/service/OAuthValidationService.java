package timefit.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

/**
 * OAuth 토큰 검증을 담당하는 서비스 (아직 사용되지 않는 클래스)
 * 나중에 실제 OAuth 제공자 API 호출로 교체 예정
 */
@Slf4j
@Service
public class OAuthValidationService {

    /**
     * OAuth 토큰 검증 및 사용자 정보 추출
     */
    public OAuthUserInfo validateToken(AuthRequestDto.CustomerOAuth request) {
        log.debug("OAuth 토큰 검증 시작: provider={}, oauthId={}",
                request.provider(), request.oauthId());

        try {
            OAuthUserInfo userInfo = validateTokenWithProvider(request);

            log.debug("OAuth 토큰 검증 완료: email={}", userInfo.email());
            return userInfo;

        } catch (Exception e) {
            log.warn("OAuth 토큰 검증 실패: provider={}, error={}",
                    request.provider(), e.getMessage());
            throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }

    // 제공자별 토큰 검증
    private OAuthUserInfo validateTokenWithProvider(AuthRequestDto.CustomerOAuth request) {
        return switch (request.provider().toUpperCase()) {
            case "GOOGLE" -> validateGoogleToken(request);
            case "KAKAO" -> validateKakaoToken(request);
            default -> throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        };
    }

    // Google OAuth 토큰 검증
    private OAuthUserInfo validateGoogleToken(AuthRequestDto.CustomerOAuth request) {
        // TODO: 실제 구현
        // API URL: https://www.googleapis.com/oauth2/v1/userinfo?access_token={accessToken}
        log.debug("Google OAuth 토큰 검증 (임시 구현)");
        return createMockUserInfo("google");
    }

    // Kakao OAuth 토큰 검증
    private OAuthUserInfo validateKakaoToken(AuthRequestDto.CustomerOAuth request) {
        // TODO: 실제 구현
        // API URL: https://kapi.kakao.com/v2/user/me
        log.debug("Kakao OAuth 토큰 검증 (임시 구현)");
        return createMockUserInfo("kakao");
    }

    // 임시 사용자 정보 생성 (개발용)
    private OAuthUserInfo createMockUserInfo(String provider) {
        return OAuthUserInfo.of(
                "customer@" + provider + ".com",
                "OAuth 쓰시는 김고객",
                "https://example.com/profile.jpg"
        );
    }

    // 토큰 유효성 검증 (빠른 검증용)
    public boolean isValidTokenFormat(AuthRequestDto.CustomerOAuth request) {
        if (request.accessToken() == null || request.accessToken().trim().isEmpty()) {
            return false;
        }

        if (request.oauthId() == null || request.oauthId().trim().isEmpty()) {
            return false;
        }

        return request.provider() != null &&
                (request.provider().equals("GOOGLE") || request.provider().equals("KAKAO"));
    }
}