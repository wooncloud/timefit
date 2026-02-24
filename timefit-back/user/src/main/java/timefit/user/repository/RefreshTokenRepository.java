package timefit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import timefit.user.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * RefreshToken Repository
 *
 * Phase 2-1: Refresh Token Rotation
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * JWT ID로 Refresh Token 조회
     *
     * @param jti JWT ID
     * @return Refresh Token (Optional)
     */
    Optional<RefreshToken> findByJti(String jti);

    /**
     * 사용자의 모든 Refresh Token 무효화 (강제 로그아웃)
     * 반환 0인 경우
     *   1. 이미 revoke된 jti → 재사용 감지 (탈취)
     *   2. jti가 DB에 아예 없음 → 위조된 토큰
     *
     * @param userId 사용자 ID
     * @return 무효화된 토큰 개수
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true " +
            "WHERE rt.userId = :userId AND rt.isRevoked = false")
    int revokeAllByUserId(@Param("userId") UUID userId);

    /**
     * 만료된 Refresh Token 삭제 (스케줄러용)
     *
     * @param now 현재 시간
     * @return 삭제된 토큰 개수
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt " +
            "WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 단일 jti 조건부 무효화 (Race Condition 방어)
     * is_revoked = false 조건을 포함해 atomic하게 UPDATE
     * 반환값이 0이면 이미 무효화된 토큰 = 재사용 감지
     * @param jti JWT ID
     * @return 무효화된 행 수 (1: 정상, 0: 이미 무효화됨)
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true " +
            "WHERE rt.jti = :jti AND rt.isRevoked = false")
    int revokeByJtiIfActive(@Param("jti") String jti);
}