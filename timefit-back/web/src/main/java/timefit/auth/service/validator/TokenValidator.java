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
 * 책임 분리:
 * - AuthTokenService: 토큰 생성, 갱신, 무효화
 * - TokenValidator: 토큰 검증 및 정보 추출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final JwtConfig jwtConfig;

    /**
     * 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValidToken(String token) {
        try {
            verifyToken(token);
            return true;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            log.debug("유효하지 않은 token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID (UUID)
     * @throws AuthException 토큰이 유효하지 않거나 UUID 파싱 실패 시
     */
    public UUID getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            String userId = decodedJWT.getSubject();
            return UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 UUID의 token 입니다: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * 토큰 만료 시간 조회
     *
     * @param token JWT 토큰
     * @return 만료 시간
     * @throws AuthException 토큰이 유효하지 않을 시
     */
    public Date getExpirationDate(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getExpiresAt();
        } catch (JWTVerificationException e) {
            log.error("토큰 만료 시간 조회 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * 토큰 검증 및 디코딩
     *
     * @param token JWT 토큰
     * @return 디코딩된 JWT
     * @throws AuthException 토큰이 만료되었거나 유효하지 않을 시
     */
    public DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtConfig.getSecretKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getIssuer())
                    .build();

            return verifier.verify(token);

        } catch (TokenExpiredException e) {
            log.warn("만료된 토큰: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.warn("토큰 검증 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }
}