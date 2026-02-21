package timefit.auth.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import timefit.user.repository.RefreshTokenRepository;

import java.time.LocalDateTime;

/**
 * 목적:
 * - 만료된 Refresh Token 자동 삭제
 * - DB 용량 관리
 * 실행 시간:
 * - 매일 새벽 3시 (서버 부하가 적은 시간)
 * - Cron: "0 0 3 * * *"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 만료된 Refresh Token 삭제
     * 실행 주기: 매일 새벽 3시
     * Cron: 초 분 시 일 월 요일
     *       0  0  3  *  *  *
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("=== Refresh Token 정리 시작 ===");

        LocalDateTime now = LocalDateTime.now();

        try {
            int deletedCount = refreshTokenRepository.deleteExpiredTokens(now);

            log.info("=== Refresh Token 정리 완료 ===");
            log.info("삭제된 토큰 개수: {}", deletedCount);
            log.info("정리 기준 시간: {}", now);

        } catch (Exception e) {
            log.error("Refresh Token 정리 중 오류 발생: {}", e.getMessage(), e);
        }
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