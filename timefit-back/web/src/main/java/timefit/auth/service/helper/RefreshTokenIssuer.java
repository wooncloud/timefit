package timefit.auth.service.helper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.service.dto.TokenPair;
import timefit.config.JwtConfig;
import timefit.user.entity.RefreshToken;
import timefit.user.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenIssuer {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenHelper jwtTokenHelper;
    private final JwtConfig jwtConfig;

    public TokenPair generateAndSaveTokenPair(UUID userId) {

        String jti = UUID.randomUUID().toString();
        TokenPair tokenPair = jwtTokenHelper.generateTokenPair(userId, jti);

        // 만료 시간 계산 (현재 시간 + Refresh Token 만료 시간)
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtConfig.getRefreshToken().getExpiration() / 1000);

        RefreshToken refreshToken = RefreshToken.of(jti, userId, expiresAt);
        refreshTokenRepository.save(refreshToken);

        log.debug("Refresh Token DB 저장 완료: jti={}, userId={}, expiresAt={}", jti, userId, expiresAt);

        return tokenPair;
    }
}
