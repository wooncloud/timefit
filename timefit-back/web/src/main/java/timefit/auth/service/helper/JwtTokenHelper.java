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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenHelper {

    private final JwtConfig jwtConfig;
    private final JwtAlgorithmProvider algorithmProvider;

    /**
     * Access Token + Refresh Token 쌍 생성
     *
     * AT와 RT는 각각 독립적인 jti를 가집니다.
     * - AT jti: InMemory 블랙리스트 등록용 (로그아웃 시 즉시 차단)
     * - RT jti: DB 저장 후 Rotation 추적용
     *
     * @param userId 사용자 ID
     * @param rtJti  Refresh Token 전용 jti (DB 저장용)
     * @return TokenPair (accessToken, refreshToken)
     */
    public TokenPair generateTokenPair(UUID userId, String rtJti) {
        // 굳이 추적 해야할 필요가 없어 액세스 토큰의 jti 는 여기서 생성 됩니다.
        String atJti = UUID.randomUUID().toString();

        String accessToken = generateToken(userId, atJti);
        String refreshToken = generateRefreshToken(userId, rtJti);

        return TokenPair.of(accessToken, refreshToken);
    }

    /**
     * Access Token 생성 (RS256)
     *
     * @param userId 사용자 ID
     * @param jti    Access Token 고유 식별자 (블랙리스트 등록용)
     * @return JWT Access Token
     */
    public String generateToken(UUID userId, String jti) {
        try {
            Algorithm algorithm = algorithmProvider.getAccessTokenAlgorithm();  // RS256
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessToken().getExpiration());

            String token = JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withJWTId(jti)
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);

            log.debug("Access Token 생성 성공 (RS256): userId={}, jti={}", userId, jti);
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
    public String generateRefreshToken(UUID userId, String jti) {
        try {
            Algorithm algorithm = algorithmProvider.getRefreshTokenAlgorithm();  // RS512
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshToken().getExpiration());

            String token = JWT.create()
                    .withIssuer(jwtConfig.getIssuer())
                    .withSubject(userId.toString())
                    .withJWTId(jti)
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