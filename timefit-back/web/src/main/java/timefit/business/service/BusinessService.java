package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessRequest;
import timefit.business.dto.BusinessResponse;
import timefit.business.entity.BusinessTypeCode;

import java.util.List;
import java.util.UUID;

/**
 * BusinessService Facade
 * - 단순 위임만 수행
 * - 트랜잭션 경계 설정
 * - 조회 작업 -> BusinessQueryService
 * - CUD 작업 -> BusinessCommandService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService {

    private final BusinessQueryService businessQueryService;
    private final BusinessCommandService businessCommandService;

    // ========================================
    // 조회 작업 (Query)
    // ========================================

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
    public List<BusinessResponse.BusinessSummary> getMyBusinesses(UUID userId) {
        return businessQueryService.getMyBusinesses(userId);
    }

    /**
     * 업체 상세 정보 조회 (공개)
     * 권한: 누구나 조회 가능
     */
    public BusinessResponse.PublicBusinessDetail getBusinessDetail(UUID businessId) {
        return businessQueryService.getBusinessDetail(businessId);
    }

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    public BusinessResponse.MembersListResult getMembersList(UUID businessId, UUID currentUserId) {
        return businessQueryService.getMembersList(businessId, currentUserId);
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    public BusinessResponse.BusinessSearchResult searchBusinesses(
            String keyword,
            BusinessTypeCode businessType,
            String region,
            int page,
            int size) {
        return businessQueryService.searchBusinesses(keyword, businessType, region, page, size);
    }

    // ========================================
    // CUD 작업 (Command)
    // ========================================

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나
     */
    @Transactional
    public BusinessResponse.BusinessDetail createBusiness(
            BusinessRequest.CreateBusiness request,
            UUID ownerId) {
        return businessCommandService.createBusiness(request, ownerId);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public BusinessResponse.BusinessProfile updateBusiness(
            UUID businessId,
            BusinessRequest.UpdateBusiness request,
            UUID currentUserId) {
        return businessCommandService.updateBusiness(businessId, request, currentUserId);
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @Transactional
    public BusinessResponse.DeleteResult deleteBusiness(
            UUID businessId,
            BusinessRequest.DeleteBusiness request,
            UUID currentUserId) {
        return businessCommandService.deleteBusiness(businessId, request, currentUserId);
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public BusinessResponse.InvitationResult inviteUser(
            UUID businessId,
            BusinessRequest.InviteUser request,
            UUID inviterUserId) {
        return businessCommandService.inviteUser(businessId, request, inviterUserId);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @Transactional
    public void changeUserRole(
            UUID businessId,
            UUID targetUserId,
            BusinessRequest.ChangeRole request,
            UUID currentUserId) {
        businessCommandService.changeUserRole(businessId, targetUserId, request, currentUserId);
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @Transactional
    public void removeMember(UUID businessId, UUID targetUserId, UUID requesterUserId) {
        businessCommandService.removeMember(businessId, targetUserId, requesterUserId);
    }
}