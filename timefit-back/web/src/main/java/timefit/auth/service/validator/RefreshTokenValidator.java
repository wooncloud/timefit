package timefit.auth.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.entity.RefreshToken;
import timefit.user.repository.RefreshTokenRepository;

import java.util.UUID;

/**
 * RefreshToken 검증 Validator
 *
 * 역할:
 * - RefreshToken 상태 검증
 * - DB 조회 및 존재 여부 확인
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenValidator {

    private final RefreshTokenRepository refreshTokenRepository;
    /**
     * RefreshToken DB 존재, 만료 여부 확인
     *
     * @param jti JWT ID
     * @return RefreshToken 엔티티
     * @throws AuthException DB에 존재하지 않을 경우
     */
    public RefreshToken validateRefreshTokenActive(String jti) {
        RefreshToken refreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new AuthException(AuthErrorCode.TOKEN_INVALID));

        // 2. 만료 확인
        if (refreshToken.isExpired()) {
            log.warn("만료된 Refresh Token: jti={}, expiresAt={}",
                    refreshToken.getJti(), refreshToken.getExpiresAt());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }
        return refreshToken;
    }
}