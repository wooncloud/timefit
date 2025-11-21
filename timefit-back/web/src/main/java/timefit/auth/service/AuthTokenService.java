package timefit.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;
import timefit.auth.service.validator.TokenValidator;
import timefit.config.JwtConfig;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 관리 서비스
 * 역할:
 * - Access Token 생성
 * - Refresh Token 생성
 * - Token 갱신
 * - Token 무효화 (로그아웃)
 * 검증 로직은 TokenValidator에 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtConfig jwtConfig;
    private final TokenValidator tokenValidator;

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

    /**
     * 토큰 갱신 (Refresh Token으로 새 Access + Refresh Token 발급)
     *
     * @param request Refresh Token 요청 DTO
     * @return 새로운 Access Token과 Refresh Token
     */
    public AuthResponseDto.TokenRefresh refreshToken(AuthRequestDto.TokenRefresh request) {
        // 1. Refresh Token 유효성 검증 (TokenValidator에 위임)
        if (!tokenValidator.isValidToken(request.refreshToken())) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        // 2. 사용자 ID 추출 (TokenValidator에 위임)
        UUID userId = tokenValidator.getUserIdFromToken(request.refreshToken());

        // 3. 새 토큰 생성
        String newAccessToken = generateToken(userId);
        String newRefreshToken = generateRefreshToken(userId);

        // 4. 만료 시간 계산 (TokenValidator에 위임)
        Date expirationDate = tokenValidator.getExpirationDate(newAccessToken);
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
     * 토큰 무효화 (로그아웃)
     * @param token JWT 토큰
     */
    public void invalidateToken(String token) {
        log.info("토큰 무효화 요청: {}", token.substring(0, Math.min(20, token.length())));
    }
}