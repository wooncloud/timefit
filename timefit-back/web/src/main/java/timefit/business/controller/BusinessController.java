package timefit.business.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.business.dto.BusinessRequest;
import timefit.business.dto.BusinessResponse;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.service.BusinessService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.List;
import java.util.UUID;

/**
 * Business Controller
 * 권한:
 * - OWNER: 모든 권한 (생성, 조회, 수정, 삭제, 구성원 관리)
 * - MANAGER: 업체 정보 조회/수정, 구성원 조회/초대 (삭제/권한변경 불가)
 * - MEMBER: 업체 정보 조회만 가능
 */
@Slf4j
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // ========================================
    // 공개 API (인증 불필요)
    // ========================================

    /**
     * 업체 상세 정보 조회 (공개)
     * 권한: 누구나 조회 가능
     */
    @GetMapping("/{businessId}")
    public ResponseData<BusinessResponse.PublicBusinessDetail> getBusinessDetail(
            @PathVariable UUID businessId) {

        log.info("업체 상세 조회 요청: businessId={}", businessId);

        BusinessResponse.PublicBusinessDetail response = businessService.getBusinessDetail(businessId);
        return ResponseData.of(response);
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    @GetMapping("/search")
    public ResponseData<BusinessResponse.BusinessSearchResult> searchBusinesses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BusinessTypeCode businessType,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("업체 검색 요청: keyword={}, businessType={}, region={}, page={}, size={}",
                keyword, businessType, region, page, size);

        BusinessResponse.BusinessSearchResult response = businessService.searchBusinesses(
                keyword, businessType, region, page, size);
        return ResponseData.of(response);
    }

    // ========================================
    // 인증 필요 API
    // ========================================

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인 (모든 권한)
     */
    @GetMapping("/my")
    public ResponseData<List<BusinessResponse.BusinessSummary>> getMyBusinesses(
            @CurrentUserId UUID currentUserId) {

        log.info("내 업체 목록 조회 요청: userId={}", currentUserId);

        List<BusinessResponse.BusinessSummary> response = businessService.getMyBusinesses(currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<BusinessResponse.BusinessDetail> createBusiness(
            @Valid @RequestBody BusinessRequest.CreateBusiness request,
            @CurrentUserId UUID ownerId) {

        log.info("업체 생성 요청: ownerId={}, businessName={}", ownerId, request.getBusinessName());

        BusinessResponse.BusinessDetail response = businessService.createBusiness(request, ownerId);
        return ResponseData.of(response);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @PutMapping("/{businessId}")
    public ResponseData<BusinessResponse.BusinessProfile> updateBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequest.UpdateBusiness request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponse.BusinessProfile response = businessService.updateBusiness(
                businessId, request, currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @DeleteMapping("/{businessId}")
    public ResponseData<BusinessResponse.DeleteResult> deleteBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequest.DeleteBusiness request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponse.DeleteResult response = businessService.deleteBusiness(
                businessId, request, currentUserId);
        return ResponseData.of(response);
    }

    // ========================================
    // 구성원 관리 API
    // ========================================

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    @GetMapping("/{businessId}/members")
    public ResponseData<BusinessResponse.MembersListResult> getMembersList(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponse.MembersListResult response = businessService.getMembersList(
                businessId, currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER (MANAGER는 MANAGER/MEMBER만 초대 가능)
     */
    @PostMapping("/{businessId}/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<BusinessResponse.InvitationResult> inviteUser(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequest.InviteUser request,
            @CurrentUserId UUID inviterUserId) {

        log.info("구성원 초대 요청: businessId={}, inviterUserId={}, email={}, role={}",
                businessId, inviterUserId, request.getEmail(), request.getRole());

        BusinessResponse.InvitationResult response = businessService.inviteUser(
                businessId, request, inviterUserId);
        return ResponseData.of(response);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @PatchMapping("/{businessId}/member/{targetUserId}/role")
    public ResponseData<Void> changeUserRole(
            @PathVariable UUID businessId,
            @PathVariable UUID targetUserId,
            @Valid @RequestBody BusinessRequest.ChangeRole request,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterUserId={}",
                businessId, targetUserId, request.getNewRole(), currentUserId);

        businessService.changeUserRole(businessId, targetUserId, request, currentUserId);
        return ResponseData.of(null);
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @DeleteMapping("/{businessId}/member/{targetUserId}")
    public ResponseData<Void> removeMember(
            @PathVariable UUID businessId,
            @PathVariable UUID targetUserId,
            @CurrentUserId UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterUserId={}",
                businessId, targetUserId, requesterUserId);

        businessService.removeMember(businessId, targetUserId, requesterUserId);
        return ResponseData.of(null);
    }
}