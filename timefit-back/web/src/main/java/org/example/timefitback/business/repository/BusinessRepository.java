package org.example.timefitback.business.repository;

import org.example.timefitback.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<Business> findByUserId(UUID userId);

    boolean existsByBusinessNumber(String businessNumber);

    boolean existsByUserId(UUID userId);

    // 사용자 정보와 함께 비즈니스 정보 조회
    @Query("SELECT b FROM Business b WHERE b.userId = :userId")
    Optional<Business> findByUserIdWithDetails(@Param("userId") UUID userId);

    // 사업자번호로 비즈니스 소유자 확인
    @Query("SELECT b.userId FROM Business b WHERE b.businessNumber = :businessNumber")
    Optional<UUID> findUserIdByBusinessNumber(@Param("businessNumber") String businessNumber);
}