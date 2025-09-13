package timefit.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBusinessRoleRepository extends JpaRepository<UserBusinessRole, UUID> {

    // 권한 조회
    Optional<UserBusinessRole> findByUserIdAndBusinessIdAndIsActive(UUID userId, UUID businessId, Boolean isActive);

    // 사용자의 비즈니스 목록
    List<UserBusinessRole> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    // 비즈니스의 구성원 목록
    List<UserBusinessRole> findByBusinessIdAndIsActive(UUID businessId, Boolean isActive);

    // 중복 체크
    boolean existsByUserIdAndBusinessIdAndIsActive(UUID userId, UUID businessId, Boolean isActive);

    // 역할별 조회
    List<UserBusinessRole> findByBusinessIdAndRoleAndIsActive(UUID businessId, BusinessRole role, Boolean isActive);
}