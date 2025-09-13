package timefit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.user.entity.User;
import timefit.user.entity.UserRole;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 기본 조회
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // OAuth 관련
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
    boolean existsByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    // 역할별 조회
    Optional<User> findByIdAndRole(UUID userId, UserRole role);
}