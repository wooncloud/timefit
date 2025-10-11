package timefit.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.menu.entity.Menu;

import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID>, MenuRepositoryCustom {

    // 기본 JPA 메서드들만 유지
}