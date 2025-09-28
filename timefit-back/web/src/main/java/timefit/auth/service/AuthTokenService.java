package timefit.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

import java.util.Date;
import java.util.UUID;

/**
 * JWT 기반 토큰 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtConfig jwtConfig;

    /**
     * Access Token 생성
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

    /**
     * 토큰에서 사용자 ID 추출
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
     * 토큰 유효성 검증
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
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getExpiresAt().before(new Date());
        } catch (JWTVerificationException | IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * 토큰 검증 및 DecodedJWT 반환
     */
    private DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtConfig.getSecretKey());
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwtConfig.getIssuer())
                    .build();

            return verifier.verify(token);

        } catch (TokenExpiredException e) {
            log.debug("Token 만료됨: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.error("JWT 검증 실패: {}", e.getMessage());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
    }

    /**
     * 토큰 무효화 (JWT 특성상 실제 무효화 불가)
     * 추후 Redis 블랙리스트로 구현 가능
     */
    public void invalidateToken(String token) {
        // JWT는 stateless 하므로 서버에서 강제 만료 불가
        // 추후 Redis 블랙리스트 구현 시 여기에 로직 추가
        log.info("토큰 무효화 요청 (현재는 로그만 기록): {}", token != null ? "토큰 있음" : "토큰 없음");
    }

    /**
     * 토큰에서 발행 시간 추출
     */
    public Date getIssuedAt(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getIssuedAt();
    }

    /**
     * 토큰에서 만료 시간 추출
     */
    public Date getExpirationDate(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getExpiresAt();
    }
}