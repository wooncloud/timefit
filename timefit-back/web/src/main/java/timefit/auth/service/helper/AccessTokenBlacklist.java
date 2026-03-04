package timefit.auth.service.helper;

import java.util.Date;

/**
 * Access Token 블랙리스트 인터페이스
 * 목적:
 * - 로그아웃된 AT의 jti를 등록하여 잔여 유효 기간 동안 재사용 차단
 * - AT는 만료 시간이 짧아(15분) DB 저장 대신 인메모리로 관리
 * - 인터페이스로 추상화하여 향후 Redis 구현체로 교체 가능

 */
public interface AccessTokenBlacklist {

    /**
     * AT jti를 블랙리스트에 등록
     * @param jti       Access Token 고유 식별자
     * @param expiresAt Access Token 만료 시간 (만료 후 자동 제거 기준)
     */
    void add(String jti, Date expiresAt);

    /**
     * AT jti 블랙리스트 등록 여부 확인
     * @param jti Access Token 고유 식별자
     * @return 블랙리스트에 등록되어 있으면 true
     */
    boolean isBlacklisted(String jti);

    /**
     * 만료된 jti 항목 일괄 제거
     * 호출 시점: TokenCleanupScheduler (매일 새벽 3시)
     */
    void removeExpired();
}