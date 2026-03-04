package timefit.auth.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.service.helper.AccessTokenBlacklist;
import timefit.user.repository.RefreshTokenRepository;

import java.time.LocalDateTime;

/**
 * 목적:
 * - 만료된 Refresh Token 삭제 (DB 관리)
 * - 만료된 Access Token 블랙리스트 항목 제거 (InMemory 관리)
 * 실행 주기:
 * - 매일 새벽 3시 (서버 부하가 적은 시간)
 * - Cron: "0 0 3 * * *"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenBlacklist accessTokenBlacklist;

    /**
     * 만료된 토큰 일괄 정리
     * 처리 항목:
     * 1. 만료된 Refresh Token 삭제
     * 2. 만료된 Access Token 블랙리스트 항목 제거 (InMemory)
     * 실행 주기: 매일 새벽 3시
     * Cron: 초 분 시 일 월 요일
     *       0  0  3  *  *  *
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("=== 토큰 정리 시작 ===");

        LocalDateTime now = LocalDateTime.now();

        try {
            // 1. 만료된 Refresh Token DB 삭제
            int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);
            log.info("만료된 Refresh Token 삭제: {}건", deletedCount);

            // 2. 만료된 Access Token 블랙리스트 항목 제거
            accessTokenBlacklist.removeExpired();

        } catch (Exception e) {
            log.error("토큰 정리 중 오류 발생: {}", e.getMessage(), e);
        }
        log.info("=== 토큰 정리 완료 ===");
    }

    /**
     * 애플리케이션 시작 시 즉시 실행 (테스트용)
     *
     * 주의: 프로덕션에서는 주석 처리 권장
     */
//    @Scheduled(initialDelay = 10000) // 10초 후 실행
//    @Transactional
//    public void cleanupOnStartup() {
//        log.info("애플리케이션 시작 시 Refresh Token 정리 실행");
//        cleanupExpiredTokens();
//    }
}