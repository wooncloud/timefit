package timefit.auth.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 유틸리티
 * 역할:
 * - 순수 토큰 생성 로직 (계산기 역할)
 * - Access Token / Refresh Token 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtConfig jwtConfig;

    /**
     * Access Token 생성
     *
     * @param userId 사용자 ID
     * @return JWT Access Token
     */
    public String generateToken(UUID userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtConfig.getSecretKey());
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

            return JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            log.error("JWT 토큰 생성 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Refresh Token 생성
     *
     * @param userId 사용자 ID
     * @return JWT Refresh Token
     */
    public String generateRefreshToken(UUID userId) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtConfig.getSecretKey());
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

            return JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .withClaim("tokenType", "refresh")
                    .sign(algorithm);

        } catch (JWTCreationException e) {
            log.error("Refresh 토큰 생성 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}