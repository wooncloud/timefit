package timefit.auth.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.service.dto.OAuthUserInfo;
import timefit.auth.service.helper.OAuthHelper;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

/**
 * OAuth 토큰 검증 Validator
 *
 * 역할:
 * - OAuth Provider별 토큰 검증
 * - 외부 API 호출 (Google, Kakao 등)
 * - 검증 실패 시 예외 처리
 *
 * 책임:
 * - ONLY 검증 로직
 * - Provider 분기 처리
 * - 외부 API 호출 (실제 구현 시)
 *
 * 향후 작업:
 * - Google/Kakao OAuth API 실제 구현
 * - RestTemplate 또는 WebClient 추가
 * - Response DTO 파싱
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthValidator {

    private final OAuthHelper oauthHelper;  // Mock 데이터 생성용 (개발용)

    /**
     * OAuth 토큰 검증 (Provider별 분기)
     *
     * @param request OAuth 요청 DTO
     * @return 검증된 사용자 정보
     * @throws AuthException 검증 실패 시
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

    /**
     * Provider별 토큰 검증 (분기 처리)
     *
     * @param request OAuth 요청 DTO
     * @return 검증된 사용자 정보
     * @throws AuthException 지원하지 않는 Provider
     */
    private OAuthUserInfo validateTokenWithProvider(AuthRequestDto.CustomerOAuth request) {
        return switch (request.provider().toUpperCase()) {
            case "GOOGLE" -> validateGoogleToken(request);
            case "KAKAO" -> validateKakaoToken(request);
            default -> throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        };
    }

    /**
     * Google OAuth 토큰 검증
     *
     * TODO: 실제 구현 필요
     *
     * 구현 가이드:
     * 1. RestTemplate 또는 WebClient 주입
     * 2. API URL: https://www.googleapis.com/oauth2/v1/userinfo?access_token={accessToken}
     * 3. GET 요청 전송
     * 4. 응답 파싱 (GoogleUserInfoResponse DTO 생성 필요)
     * 5. OAuthUserInfo로 변환하여 반환
     *
     * @param request OAuth 요청 DTO
     * @return 검증된 사용자 정보
     */
    private OAuthUserInfo validateGoogleToken(AuthRequestDto.CustomerOAuth request) {
        log.debug("Google OAuth 토큰 검증 (임시 구현)");

        // 현재: Mock 데이터 반환 (개발용)
        return oauthHelper.createMockUserInfo("google");

        /* 실제 구현 예시:
        String apiUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token="
                         + request.accessToken();

        try {
            GoogleUserInfoResponse response =
                restTemplate.getForObject(apiUrl, GoogleUserInfoResponse.class);

            if (response == null) {
                throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
            }

            return OAuthUserInfo.of(
                response.getEmail(),
                response.getName(),
                response.getPicture()
            );

        } catch (RestClientException e) {
            log.error("Google OAuth API 호출 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        }
        */
    }

    /**
     * Kakao OAuth 토큰 검증
     *
     * TODO: 실제 구현 필요
     *
     * 구현 가이드:
     * 1. RestTemplate 또는 WebClient 주입
     * 2. API URL: https://kapi.kakao.com/v2/user/me
     * 3. Header: Authorization: Bearer {access_token}
     * 4. GET 요청 전송
     * 5. 응답 파싱 (KakaoUserInfoResponse DTO 생성 필요)
     * 6. OAuthUserInfo로 변환하여 반환
     *
     * @param request OAuth 요청 DTO
     * @return 검증된 사용자 정보
     */
    private OAuthUserInfo validateKakaoToken(AuthRequestDto.CustomerOAuth request) {
        log.debug("Kakao OAuth 토큰 검증 (임시 구현)");

        // 현재: Mock 데이터 반환 (개발용)
        return oauthHelper.createMockUserInfo("kakao");

        /* 실제 구현 예시:
        String apiUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + request.accessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfoResponse> response =
                restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfoResponse.class
                );

            if (response.getBody() == null) {
                throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
            }

            KakaoUserInfoResponse.KakaoAccount account = response.getBody().getKakaoAccount();

            return OAuthUserInfo.of(
                account.getEmail(),
                account.getProfile().getNickname(),
                account.getProfile().getProfileImageUrl()
            );

        } catch (RestClientException e) {
            log.error("Kakao OAuth API 호출 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.OAUTH_PROVIDER_ERROR);
        }
        */
    }
}