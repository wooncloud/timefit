package timefit.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 임시 토큰 관리 서비스 (JWT 구현 전까지 사용)
 * 나중에 JWT + Redis로 교체 예정
 */
@Slf4j
@Service
public class AuthTokenService {

    // 메모리 기반 임시 저장소 (실제로는 Redis 사용 예정)
    private final Map<String, UUID> tokenToUserMap = new ConcurrentHashMap<>();
    private final Map<UUID, String> userToTokenMap = new ConcurrentHashMap<>();

    /**
     * 임시 토큰 생성
     */
    public String generateToken(UUID userId) {
        // 기존 토큰 제거
        removeExistingToken(userId);

        // 새로운 토큰 생성
        String token = createNewToken();

        // 저장
        storeToken(token, userId);

        log.debug("임시 토큰 생성: userId={}", userId);
        return token;
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public UUID getUserIdFromToken(String token) {
        UUID userId = tokenToUserMap.get(token);
        if (userId == null) {
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }
        return userId;
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean isValidToken(String token) {
        return tokenToUserMap.containsKey(token);
    }

    /**
     * 토큰 무효화
     */
    public void invalidateToken(String token) {
        UUID userId = tokenToUserMap.remove(token);
        if (userId != null) {
            userToTokenMap.remove(userId);
            log.debug("임시 토큰 무효화: userId={}", userId);
        }
    }

    /**
     * 사용자의 모든 토큰 무효화
     */
    public void invalidateUserTokens(UUID userId) {
        String token = userToTokenMap.remove(userId);
        if (token != null) {
            tokenToUserMap.remove(token);
            log.debug("사용자 토큰 무효화: userId={}", userId);
        }
    }

    /**
     * 헬스 체크용 - 저장된 토큰 수 반환
     */
    public int getActiveTokenCount() {
        return tokenToUserMap.size();
    }

    // ===== Private Methods =====

    private void removeExistingToken(UUID userId) {
        String existingToken = userToTokenMap.get(userId);
        if (existingToken != null) {
            tokenToUserMap.remove(existingToken);
        }
    }

    private String createNewToken() {
        return "temp_" + UUID.randomUUID().toString().replace("-", "");
    }

    private void storeToken(String token, UUID userId) {
        tokenToUserMap.put(token, userId);
        userToTokenMap.put(userId, token);
    }
}