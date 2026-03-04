package timefit.auth.service.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Access Token 블랙리스트 인메모리 구현체
 * 저장 구조:
 * - ConcurrentHashMap<jti, 만료 epoch ms>
 * - Spring Singleton Bean으로 등록되어 JVM Heap에서 관리
 * 한계 (인식하고 감수):
 * - 서버 재시작 시 블랙리스트 소멸
 *   → 재시작 직후 로그아웃된 AT가 15분 내라면 일시적으로 유효해짐
 * - 멀티 서버 환경에서 서버 간 공유 불가
 *   → 단일 서버 MVP에서는 무관, 확장 시 RedisAccessTokenBlacklist로 교체
 */
@Slf4j
@Component
public class InMemoryAccessTokenBlacklist implements AccessTokenBlacklist {

    // key: AT jti, value: 만료 시각 (epoch ms)
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * AT jti를 블랙리스트에 등록
     *
     * @param jti       Access Token 고유 식별자
     * @param expiresAt Access Token 만료 시간
     */
    @Override
    public void add(String jti, Date expiresAt) {
        blacklist.put(jti, expiresAt.getTime());
        log.debug("AT 블랙리스트 등록: jti={}, expiresAt={}", jti, expiresAt);
    }

    /**
     * AT jti 블랙리스트 등록 여부 확인
     *
     * Lazy Eviction:
     * - 조회 시점에 만료된 항목은 즉시 제거 후 false 반환
     * - 스케줄러 의존 없이 메모리 누수 방지 보조
     *
     * @param jti Access Token 고유 식별자
     * @return 블랙리스트에 등록되어 있으면 true
     */
    @Override
    public boolean isBlacklisted(String jti) {
        Long expiresAtMs = blacklist.get(jti);

        if (expiresAtMs == null) {
            return false;
        }

        // 만료된 항목이면 제거 후 false 반환 (Lazy Eviction)
        if (System.currentTimeMillis() > expiresAtMs) {
            blacklist.remove(jti);
            log.debug("만료된 AT 블랙리스트 항목 제거 (Lazy): jti={}", jti);
            return false;
        }

        return true;
    }

    /**
     * 만료된 jti 항목 일괄 제거
     *
     * 호출 시점: TokenCleanupScheduler (매일 새벽 3시)
     * Lazy Eviction과 병행하여 메모리 누수 방지
     */
    @Override
    public void removeExpired() {
        long now = System.currentTimeMillis();
        int removedCount = 0;

        Iterator<Map.Entry<String, Long>> iterator = blacklist.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (now > entry.getValue()) {
                iterator.remove();
                removedCount++;
            }
        }

        log.info("AT 블랙리스트 만료 항목 제거: count={}, 잔여={}", removedCount, blacklist.size());
    }
}