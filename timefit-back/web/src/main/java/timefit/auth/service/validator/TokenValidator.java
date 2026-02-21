package timefit.auth.service.validator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.provider.JwtAlgorithmProvider;
import timefit.auth.service.dto.RefreshTokenClaims;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 검증 전담 Validator
 * 역할:
 * - 토큰 유효성 검증
 * - 토큰 파싱 및 정보 추출
 * - 토큰 만료 시간 조회
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithmProvider jwtAlgorithmProvider;

    /**
     * Access Token 유효성 검증
     *
     * @param token JWT Access Token
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token) {
        try {
            verifyAccessToken(token);
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.debug("유효하지 않은 Access Token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Access Token에서 사용자 ID 추출
     *
     * @param token JWT Access Token
     * @return 사용자 ID (UUID)
     * @throws AuthException 토큰이 유효하지 않거나 UUID 파싱 실패 시
     */
    public UUID getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = verifyAccessToken(token);
        return UUID.fromString(decodedJWT.getSubject());
    }

    /**
     * Refresh Token에서 jti와 userId 한 번에 추출
     *
     * 성능 최적화:
     * - 토큰을 1번만 파싱 (기존: 2-3번)
     * - jti + userId를 동시에 추출
     *
     * @param token Refresh Token
     * @return RefreshTokenClaims (jti, userId)
     * @throws AuthException 토큰이 유효하지 않을 경우
     */
    public RefreshTokenClaims extractRefreshTokenClaims(String token) {
        // 토큰 유효성 검사
        DecodedJWT decodedJWT = verifyRefreshToken(token);

        // jti 추출 및 검증
        String jti = decodedJWT.getId();
        if (jti == null || jti.isBlank()) {
            log.error("Refresh Token에 jti가 없습니다");
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // userId 추출
        UUID userId = UUID.fromString(decodedJWT.getSubject());
        log.debug("Refresh Token Claims 추출 완료: jti={}, userId={}", jti, userId);

        return RefreshTokenClaims.of(jti, userId);
    }

    /**
     * 토큰 만료 시간 조회
     *
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date getExpirationDate(String token) {
        // 검증 없이 디코딩만 수행 (만료 시간 조회용)
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getExpiresAt();
    }
    // ========== Private 검증 로직 ==========

    /**
     * Access Token 검증 및 디코딩 (RS256)
     *
     * @param token JWT Access Token
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    private DecodedJWT verifyAccessToken(String token) {
        Algorithm algorithm = jwtAlgorithmProvider.getAccessTokenAlgorithm();
        DecodedJWT decodedJWT = verifyToken(token, algorithm);
        log.debug("Access Token 검증 성공 (RS256)");
        return decodedJWT;
    }

    /**
     * Refresh Token 검증 및 디코딩 (RS512)
     *
     * @param token JWT Refresh Token
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    private DecodedJWT verifyRefreshToken(String token) {
        Algorithm algorithm = jwtAlgorithmProvider.getRefreshTokenAlgorithm();
        DecodedJWT decodedJWT = verifyToken(token, algorithm);
        log.debug("Refresh Token 검증 성공 (RS512)");
        return decodedJWT;
    }

    /**
     * JWT 검증 및 디코딩 (통합 메서드)
     *
     * @param token JWT 토큰
     * @param algorithm 검증 알고리즘 (RS256 또는 RS512)
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    private DecodedJWT verifyToken(String token, Algorithm algorithm) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getIssuer())
                    .build();

            return verifier.verify(token);

        } catch (TokenExpiredException e) {
            log.warn("만료된 Token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.warn("Token 검증 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

}