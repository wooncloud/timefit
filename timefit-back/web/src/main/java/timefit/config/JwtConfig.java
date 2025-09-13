package timefit.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class JwtConfig {

    // application.properties
    @Value("${jwt.secret:timefit-secret-key-for-development-only-change-in-production}")
    private String secretKey;

    //  Access Token 만료 시간 900000 = 15m
    @Value("${jwt.access-token.expiration:900000}")
    private Long accessTokenExpiration;

    // Refresh Token 만료 시간 (밀리초) 604800000 = 7days
    @Value("${jwt.refresh-token.expiration:604800000}")
    private Long refreshTokenExpiration;

    // JWT 토큰 발행자 정보
    @Value("${jwt.issuer:timefit}")
    private String issuer;


    // JWT Authorization 헤더 이름
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // JWT 토큰 접두사
    public static final String TOKEN_PREFIX = "Bearer ";

    // JWT 토큰 접두사 길이
    public static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
}