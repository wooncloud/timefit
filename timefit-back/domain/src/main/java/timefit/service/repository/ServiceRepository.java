package timefit.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.service.entity.Service;

import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID>, ServiceRepositoryCustom {

    // 기본 JPA 메서드들만 유지
}