package timefit.auth.service.dto;

import java.util.UUID;

/**
 * Refresh Token Claims (jti + userId)
 * 용도:
 * - extractRefreshTokenClaims() 반환 타입
 * - 한 번의 토큰 파싱으로 jti와 userId 동시 추출
 */
public record RefreshTokenClaims(String jti, UUID userId) {
    public static RefreshTokenClaims of(String jti, UUID userId) {
        return new RefreshTokenClaims(jti, userId);
    }
}
