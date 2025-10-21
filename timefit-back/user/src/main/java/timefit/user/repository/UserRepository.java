package timefit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.user.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // 기본 조회
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // OAuth 관련
    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
}