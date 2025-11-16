package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.BusinessTypeCode;

import java.util.UUID;

/**
 * BusinessService Facade
 * - 단순 위임만 수행
 * - 트랜잭션 경계 설정
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
     * 공개 업체 상세 조회
     * 권한: 누구나 조회 가능
     */
    public BusinessResponseDto.PublicBusinessResponse getPublicBusinessDetail(UUID businessId) {
        return businessQueryService.getPublicBusinessDetail(businessId);
    }

    /**
     * 사업자용 업체 상세 조회
     * 권한: OWNER/MANAGER/MEMBER
     */
    public BusinessResponseDto.BusinessResponse getBusinessProfile(
            UUID businessId,
            UUID currentUserId) {
        return businessQueryService.getBusinessProfile(businessId, currentUserId);
    }

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
    public BusinessResponseDto.BusinessListResponse getMyBusinesses(UUID currentUserId) {
        return businessQueryService.getMyBusinesses(currentUserId);
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    public BusinessResponseDto.BusinessListResponse searchBusinesses(
            String keyword,
            BusinessTypeCode businessType,
            String region,
            int page,
            int size) {
        return businessQueryService.searchBusinesses(keyword, businessType, region, page, size);
    }

    /**
     * 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER
     */
    public BusinessResponseDto.MemberListResponse getMembersList(
            UUID businessId,
            UUID currentUserId) {
        return businessQueryService.getMembersList(businessId, currentUserId);
    }

    // ========================================
    // 변경 작업 (Command)
    // ========================================

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나
     */
    @Transactional
    public BusinessResponseDto.BusinessResponse createBusiness(
            BusinessRequestDto.CreateBusinessRequest request,
            UUID ownerId) {
        return businessCommandService.createBusiness(request, ownerId);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @Transactional
    public BusinessResponseDto.BusinessResponse updateBusiness(
            UUID businessId,
            BusinessRequestDto.UpdateBusinessRequest request,
            UUID currentUserId) {
        return businessCommandService.updateBusiness(businessId, request, currentUserId);
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @Transactional
    public BusinessResponseDto.DeleteBusinessResponse deleteBusiness(
            UUID businessId,
            BusinessRequestDto.DeleteBusinessRequest request,
            UUID currentUserId) {
        return businessCommandService.deleteBusiness(businessId, request, currentUserId);
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     * 변경사항: role 파라미터 제거 (MEMBER로 고정)
     */
    @Transactional
    public BusinessResponseDto.MemberListResponse.MemberResponse inviteMember(
            UUID businessId,
            BusinessRequestDto.InviteMemberRequest request,
            UUID inviterUserId) {
        return businessCommandService.inviteMember(businessId, request, inviterUserId);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @Transactional
    public void changeMemberRole(
            UUID businessId,
            UUID targetUserId,
            BusinessRequestDto.ChangeMemberRoleRequest request,
            UUID currentUserId) {
        businessCommandService.changeMemberRole(businessId, targetUserId, request, currentUserId);
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @Transactional
    public void removeMember(
            UUID businessId,
            UUID targetUserId,
            UUID requesterUserId) {
        businessCommandService.removeMember(businessId, targetUserId, requesterUserId);
    }

    /**
     * 구성원 활성화
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public void activateMember(
            UUID businessId,
            UUID targetUserId,
            UUID currentUserId) {
        businessCommandService.activateMember(businessId, targetUserId, currentUserId);
    }

    /**
     * 구성원 비활성화
     * 권한: OWNER, MANAGER
     */
    @Transactional
    public void deactivateMember(
            UUID businessId,
            UUID targetUserId,
            UUID currentUserId) {
        businessCommandService.deactivateMember(businessId, targetUserId, currentUserId);
    }
}