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
     * Refresh Token 유효성 검증
     *
     * @param token JWT Refresh Token
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidRefreshToken(String token) {
        try {
            verifyRefreshToken(token);
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.debug("유효하지 않은 Refresh Token: {}", e.getMessage());
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
        try {
            DecodedJWT decodedJWT = verifyAccessToken(token);
            String userId = decodedJWT.getSubject();
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 UUID의 token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Refresh Token에서 사용자 ID 추출
     *
     * @param token JWT Refresh Token
     * @return 사용자 ID (UUID)
     * @throws AuthException 토큰이 유효하지 않거나 UUID 파싱 실패 시
     */
    public UUID getUserIdFromRefreshToken(String token) {
        try {
            DecodedJWT decodedJWT = verifyRefreshToken(token);
            String userId = decodedJWT.getSubject();
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 UUID의 Refresh Token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
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

    /**
     * Access Token 검증 및 디코딩
     *
     * @param token JWT Access Token
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    private DecodedJWT verifyAccessToken(String token) {
        try {
            Algorithm algorithm = jwtAlgorithmProvider.getAccessTokenAlgorithm();  // RS256
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getIssuer())
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            log.debug("Access Token 검증 성공 (RS256)");
            return decodedJWT;

        } catch (TokenExpiredException e) {
            log.warn("만료된 Access Token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.warn("Access Token 검증 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * Refresh Token 검증 및 디코딩
     *
     * @param token JWT Refresh Token
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    private DecodedJWT verifyRefreshToken(String token) {
        try {
            Algorithm algorithm = jwtAlgorithmProvider.getRefreshTokenAlgorithm();  // RS512
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getIssuer())
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            log.debug("Refresh Token 검증 성공 (RS512)");
            return decodedJWT;

        } catch (TokenExpiredException e) {
            log.warn("만료된 Refresh Token: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.warn("Refresh Token 검증 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}