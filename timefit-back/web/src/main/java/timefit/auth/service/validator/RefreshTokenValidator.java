package timefit.auth.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.auth.service.helper.RefreshTokenHelper;
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
    private final RefreshTokenHelper refreshTokenHelper;
    /**
     * RefreshToken DB 존재 여부 확인
     *
     * @param jti JWT ID
     * @return RefreshToken 엔티티
     * @throws AuthException DB에 존재하지 않을 경우
     */
    public RefreshToken validateJtiExists(String jti) {
        return refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> {
                    log.warn("DB에 존재하지 않는 Refresh Token: jti={}", jti);
                    return new AuthException(AuthErrorCode.TOKEN_INVALID);
                });
    }

    /**
     * RefreshToken Rotation 검증 (재사용 감지 + 만료 확인)
     *
     * 재사용 감지 시 자동으로 보안 조치 수행:
     * - 해당 사용자의 모든 토큰 무효화
     *
     * @param refreshToken RefreshToken 엔티티
     * @param userId 사용자 ID
     * @throws AuthException 재사용 또는 만료된 토큰
     */
    public void validateForRotation(RefreshToken refreshToken, UUID userId) {

        // 1. 재사용 감지
        if (refreshToken.getIsRevoked()) {
            log.error("🚨 Refresh Token 재사용 감지: jti={}, userId={}",
                    refreshToken.getJti(), userId);

            // 보안 조치: 해당 사용자의 모든 토큰 무효화 (Helper)
            refreshTokenHelper.revokeAllByUserId(userId);

            throw new AuthException(AuthErrorCode.TOKEN_REUSED);
        }

        // 2. 만료 확인
        if (refreshToken.isExpired()) {
            log.warn("만료된 Refresh Token: jti={}, expiresAt={}",
                    refreshToken.getJti(), refreshToken.getExpiresAt());
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }
    }
}