package timefit.invitation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import timefit.invitation.entity.Invitation;
import timefit.invitation.entity.InvitationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    /**
     * 토큰으로 초대 조회
     */
    Optional<Invitation> findByToken(String token);

    /**
     * 업체 + 이메일 + 상태로 초대 존재 확인
     * (중복 초대 방지용)
     */
    boolean existsByBusinessIdAndEmailAndStatus(
            UUID businessId,
            String email,
            InvitationStatus status
    );

    /**
     * 업체 + 이메일 + 상태로 초대 조회
     * (재발송 시 기존 PENDING 초대 찾기)
     */
    Optional<Invitation> findByBusinessIdAndEmailAndStatus(
            UUID businessId,
            String email,
            InvitationStatus status
    );

    /**
     * 업체별 전체 초대 목록 조회
     * (모달에서 초대중인 사람 표시)
     */
    List<Invitation> findByBusinessId(UUID businessId);
}