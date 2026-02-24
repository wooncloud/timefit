package timefit.auth.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;
import timefit.user.entity.RefreshToken;
import timefit.user.repository.RefreshTokenRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * RefreshToken 처리 Helper
 *
 * 역할:
 * - RefreshToken 무효화 처리
 * - 예외를 던지지 않음 (내부에서 처리)
 * - 중복 로직 제거 (CommandService, Validator에서 공통 사용)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenHelper {

    private final RefreshTokenRepository refreshTokenRepository;
    private static final int REVOKE_SUCCESS = 1;
    /**
     * jti로 RefreshToken 무효화 (단일 디바이스 로그아웃)
     *
     * 특징:
     * - 예외를 던지지 않음 (로그아웃은 항상 성공)
     * - 존재하지 않아도 성공 처리 (이미 로그아웃됨)
     * - 유효하지 않은 토큰도 무시 (사용자 경험)
     *
     * @param jti JWT ID
     */
    public void revokeByJti(String jti, UUID userId) {
        int revoked = refreshTokenRepository.revokeByJtiIfActive(jti);

        if (revoked == REVOKE_SUCCESS) {
            log.info("로그아웃 완료: jti={}, userId={}", jti, userId);
        }
        else {
            log.warn("이미 무효화된 Refresh Token: jti={}", jti);
        }
    }

    public void rotateRefreshToken(String jti, UUID userId) {
        int revoked = refreshTokenRepository.revokeByJtiIfActive(jti);

        if (revoked != REVOKE_SUCCESS) {
            log.error("🚨 Refresh Token 재사용 감지: jti={}, userId={}", jti, userId);
            revokeAllByUserId(userId);
            throw new AuthException(AuthErrorCode.TOKEN_REUSED);
        }

        log.debug("Refresh Token 무효화 완료: jti={}", jti);
    }

    /**
     * 사용자의 모든 RefreshToken 무효화 (전체 디바이스 로그아웃)
     *
     * 사용 시나리오:
     * - Refresh Token 재사용 감지 시 보안 조치 (Validator에서 호출)
     *
     * @param userId 사용자 ID
     */
    public void revokeAllByUserId(UUID userId) {
        try {
            int revokedCount = refreshTokenRepository.revokeAllByUserId(userId);
            log.error("🚨 보안 조치: 사용자의 모든 토큰 무효화 완료 - userId={}, count={}",
                    userId, revokedCount);
        } catch (Exception e) {
            log.error("전체 로그아웃 처리 중 오류: userId={}, error={}", userId, e.getMessage());
            // 예외를 던지지 않음
        }
    }
}