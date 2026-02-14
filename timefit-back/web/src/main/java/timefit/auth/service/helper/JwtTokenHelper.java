package timefit.auth.service.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.provider.JwtAlgorithmProvider;
import timefit.auth.service.dto.TokenPair;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 Helper
 *
 * Phase 1: RSA 비대칭키
 * - Access Token: RS256 (빠른 검증)
 * - Refresh Token: RS512 (강한 보안)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenHelper {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithmProvider algorithmProvider;

    /**
     * Access Token + Refresh Token 쌍 생성
     *
     * @param userId 사용자 ID
     * @return TokenPair (accessToken, refreshToken)
     */
    public TokenPair generateTokenPair(UUID userId) {
        String accessToken = generateToken(userId);
        String refreshToken = generateRefreshToken(userId);

        return TokenPair.of(accessToken, refreshToken);
    }

    /**
     * Access Token 생성 (RS256)
     *
     * @param userId 사용자 ID
     * @return JWT Access Token
     */
    public String generateToken(UUID userId) {
        try {
            Algorithm algorithm = algorithmProvider.getAccessTokenAlgorithm();  // RS256
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessToken().getExpiration());

            String token = JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);

            log.debug("Access Token 생성 성공 (RS256): userId={}", userId);
            return token;

        } catch (JWTCreationException e) {
            log.error("Access Token 생성 실패: userId={}, error={}", userId, e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Refresh Token 생성 (RS512)
     *
     * @param userId 사용자 ID
     * @return JWT Refresh Token
     */
    public String generateRefreshToken(UUID userId) {
        try {
            Algorithm algorithm = algorithmProvider.getRefreshTokenAlgorithm();  // RS512
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshToken().getExpiration());

            String token = JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .withClaim("tokenType", "refresh")
                    .sign(algorithm);

            log.debug("Refresh Token 생성 성공 (RS512): userId={}", userId);
            return token;

        } catch (JWTCreationException e) {
            log.error("Refresh Token 생성 실패: userId={}, error={}", userId, e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}