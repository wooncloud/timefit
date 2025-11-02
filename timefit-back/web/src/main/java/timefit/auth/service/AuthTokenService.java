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
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

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
     * 토큰 갱신 (Refresh Token으로 새 Access + Refresh Token 발급)
     */
    public AuthResponseDto.TokenRefresh refreshToken(AuthRequestDto.TokenRefresh request) {
        // 1. Refresh Token 유효성 검증
        if (!isValidToken(request.getRefreshToken())) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 2. 사용자 ID 추출
        UUID userId = getUserIdFromToken(request.getRefreshToken());

        // 3. 새 토큰 생성
        String newAccessToken = generateToken(userId);
        String newRefreshToken = generateRefreshToken(userId);

        // 4. 만료 시간 계산
        Date expirationDate = getExpirationDate(newAccessToken);
        long expiresIn = (expirationDate.getTime() - System.currentTimeMillis()) / 1000;

        // 5. DTO 반환
        return AuthResponseDto.TokenRefresh.of(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                expiresIn
        );
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
            log.error("유효하지 않은 UUID의 token입니다: {}", e.getMessage());
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
     * 토큰 만료 시간 조회
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
     * 토큰 무효화 (로그아웃)
     * 현재는 로그만 기록 (추후 Redis 등으로 블랙리스트 관리 가능)
     */
    public void invalidateToken(String token) {
        log.info("토큰 무효화 요청: {}", token.substring(0, Math.min(20, token.length())));
        // TODO: Redis 블랙리스트 추가
    }

    /**
     * 토큰 검증
     */
    private DecodedJWT verifyToken(String token) {
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