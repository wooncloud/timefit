package timefit.business.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBusinessRoleRepository extends JpaRepository<UserBusinessRole, UUID> {

    // 권한 조회 (only 활성)
    Optional<UserBusinessRole> findByUserIdAndBusinessIdAndIsActive(UUID userId, UUID businessId, Boolean isActive);

    // 권한 조회 (활성/비활성 모두)
    Optional<UserBusinessRole> findByUserIdAndBusinessId(UUID userId, UUID businessId);

    /**
     * 사용자의 비즈니스 목록 조회 (Business 정보 필요)
     * - Sidebar용
     * - N+1 방지: Business Fetch Join
     */
    @EntityGraph(attributePaths = {"business"})
    List<UserBusinessRole> findByUserIdAndIsActive(UUID userId, Boolean isActive);

    /**
     * 비즈니스의 구성원 목록 조회 (User 정보 필요)
     * - 팀 관리 화면용
     * - N+1 방지: User Fetch Join
     */
    @EntityGraph(attributePaths = {"user", "invitedBy"})
    List<UserBusinessRole> findByBusinessIdAndIsActiveOrderByJoinedAtAsc(
            UUID businessId, Boolean isActive);



    /**
     * 역할별 조회 (User 정보 필요)
     * - 특정 역할의 구성원 조회 시
     * - N+1 방지: User Fetch Join
     */
    @EntityGraph(attributePaths = {"user"})
    List<UserBusinessRole> findByBusinessIdAndRoleAndIsActive(UUID businessId, BusinessRole role, Boolean isActive);
}