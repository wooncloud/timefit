package timefit.auth.service.dto;

import java.util.Date;
import java.util.UUID;

/**
 * Access Token Claims DTO
 * - AT를 1번만 파싱해서 필요한 값을 한 번에 추출
 * - Filter에서 userId, jti, expiresAt을 각각 따로 파싱하는 비용 제거
 */
public record AccessTokenClaims(
        UUID userId,
        String jti,
        Date expiresAt
) {
    public static AccessTokenClaims of(UUID userId, String jti, Date expiresAt) {
        return new AccessTokenClaims(userId, jti, expiresAt);
    }
}