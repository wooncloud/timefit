package org.example.timefitback.business.repository;

import org.example.timefitback.business.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    boolean existsByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    // 마지막 로그인 시간 업데이트 (JPQL 차후 수정할 것)
    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :loginAt, u.version = u.version + 1 WHERE u.id = :userId")
    int updateLastLoginAt(@Param("userId") UUID userId, @Param("loginAt") LocalDateTime loginAt);

    // 이메일로 사용자 역할 확인
    @Query("SELECT u.role FROM User u WHERE u.email = :email")
    Optional<User.UserRole> findRoleByEmail(@Param("email") String email);
}