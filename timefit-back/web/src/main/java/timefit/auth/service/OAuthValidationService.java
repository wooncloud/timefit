package timefit.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.auth.dto.AuthRequestDto;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

/**
 * OAuth 토큰 검증을 담당하는 서비스
 * 나중에 실제 OAuth 제공자 API 호출로 교체 예정
 */
@Slf4j
@Service
public class OAuthValidationService {

    /**
     * OAuth 토큰 검증 및 사용자 정보 추출
     */
    public AuthRequestDto.OAuthUserInfo validateToken(AuthRequestDto.CustomerOAuth request) {
        log.debug("OAuth 토큰 검증 시작: provider={}, oauthId={}",
                request.getProvider(), request.getOauthId());

        try {
            // TODO: 실제 구현에서는 OAuth 제공자 API 호출하여 토큰 검증
            AuthRequestDto.OAuthUserInfo userInfo = validateTokenWithProvider(request);

            log.debug("OAuth 토큰 검증 완료: email={}", userInfo.getEmail());
            return userInfo;

        } catch (Exception e) {
            log.warn("OAuth 토큰 검증 실패: provider={}, error={}",
                    request.getProvider(), e.getMessage());
            throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        }
    }

    /**
     * 제공자별 토큰 검증
     */
    private AuthRequestDto.OAuthUserInfo validateTokenWithProvider(AuthRequestDto.CustomerOAuth request) {
        return switch (request.getProvider().toUpperCase()) {
            case "GOOGLE" -> validateGoogleToken(request);
            case "KAKAO" -> validateKakaoToken(request);
            default -> throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        };
    }

    /**
     * Google OAuth 토큰 검증
     */
    private AuthRequestDto.OAuthUserInfo validateGoogleToken(AuthRequestDto.CustomerOAuth request) {
        // TODO: 실제 구현
        // API URL: https://www.googleapis.com/oauth2/v1/userinfo?access_token={accessToken}
        // 응답에서 email, name, picture 추출

        // 임시 구현 - 실제로는 Google API 호출 필요
        log.debug("Google OAuth 토큰 검증 (임시 구현)");
        return createMockUserInfo("google");
    }

    /**
     * Kakao OAuth 토큰 검증
     */
    private AuthRequestDto.OAuthUserInfo validateKakaoToken(AuthRequestDto.CustomerOAuth request) {
        // TODO: 실제 구현
        // API URL: https://kapi.kakao.com/v2/user/me
        // Header: Authorization: Bearer {accessToken}
        // 응답에서 kakao_account.email, properties.nickname, properties.profile_image 추출

        // 임시 구현 - 실제로는 Kakao API 호출 필요
        log.debug("Kakao OAuth 토큰 검증 (임시 구현)");
        return createMockUserInfo("kakao");
    }

    /**
     * 임시 사용자 정보 생성 (개발용)
     */
    private AuthRequestDto.OAuthUserInfo createMockUserInfo(String provider) {
        return AuthRequestDto.OAuthUserInfo.of(
                "customer@" + provider + ".com",
                "OAuth 쓰시는 김고객",
                "https://example.com/profile.jpg"
        );
    }

    /**
     * 토큰 유효성 검증 (빠른 검증용)
     */
    public boolean isValidTokenFormat(AuthRequestDto.CustomerOAuth request) {
        if (request.getAccessToken() == null || request.getAccessToken().trim().isEmpty()) {
            return false;
        }

        if (request.getOauthId() == null || request.getOauthId().trim().isEmpty()) {
            return false;
        }

        return request.getProvider() != null &&
                (request.getProvider().equals("GOOGLE") || request.getProvider().equals("KAKAO"));
    }
}