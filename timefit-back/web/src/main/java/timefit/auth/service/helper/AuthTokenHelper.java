package timefit.auth.service.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.auth.service.util.JwtTokenUtil;

import java.util.UUID;

/**
 * Auth 토큰 생성 헬퍼
 *
 * 목적:
 * - Access Token + Refresh Token 생성 로직 재사용
 * - AuthService에서 중복 코드 제거
 */
@Component
@RequiredArgsConstructor
public class AuthTokenHelper {

    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Access Token + Refresh Token 생성
     *
     * @param userId 사용자 ID
     * @return TokenPair (accessToken, refreshToken)
     */
    public TokenPair generateTokenPair(UUID userId) {
        String accessToken = jwtTokenUtil.generateToken(userId);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userId);

        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * Token Pair (Access + Refresh)
     */
    @Getter
    @RequiredArgsConstructor
    public static class TokenPair {
        private final String accessToken;
        private final String refreshToken;
    }
}