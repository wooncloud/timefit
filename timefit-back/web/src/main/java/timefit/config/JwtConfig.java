package timefit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 설정
 *
 * Phase 1: RSA (비대칭키)
 * - Access Token: RS256 (2048 bits)
 * - Refresh Token: RS512 (4096 bits)
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {

    private String secret;      // HMAC용 (하위 호환, 향후 제거 예정)
    private String issuer;      // 토큰 발행자

    private Access access;      // Access Token 설정
    private Refresh refresh;    // Refresh Token 설정
    private AccessToken accessToken;    // Access Token 만료 시간
    private RefreshToken refreshToken;  // Refresh Token 만료 시간

    /**
     * Access Token 설정 (RS256)
     */
    @Getter
    @Setter
    public static class Access {
        private String privateKeyPath;  // Private Key 경로
        private String publicKeyPath;   // Public Key 경로
    }

    /**
     * Refresh Token 설정 (RS512)
     */
    @Getter
    @Setter
    public static class Refresh {
        private String privateKeyPath;  // Private Key 경로
        private String publicKeyPath;   // Public Key 경로
    }

    /**
     * Access Token 만료 시간
     */
    @Getter
    @Setter
    public static class AccessToken {
        private Long expiration;  // 밀리초
    }

    /**
     * Refresh Token 만료 시간
     */
    @Getter
    @Setter
    public static class RefreshToken {
        private Long expiration;  // 밀리초
    }

    // ========== 상수 ==========

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
}