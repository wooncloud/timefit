package timefit.business.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.service.BusinessService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;

import java.util.UUID;

/**
 * Business Controller
 * 권한:
 * - OWNER: 모든 권한
 * - MANAGER: 업체 정보 조회/수정, 구성원 조회/초대
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
    public ResponseData<BusinessResponseDto.PublicBusinessResponse> getBusinessDetail(
            @PathVariable UUID businessId) {

        log.info("업체 상세 조회 요청: businessId={}", businessId);

        BusinessResponseDto.PublicBusinessResponse response = businessService.getPublicBusinessDetail(businessId);
        return ResponseData.of(response);
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    @GetMapping("/search")
    public ResponseData<BusinessResponseDto.BusinessListResponse> searchBusinesses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BusinessTypeCode businessType,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("업체 검색 요청: keyword={}, businessType={}, region={}, page={}, size={}",
                keyword, businessType, region, page, size);

        BusinessResponseDto.BusinessListResponse response = businessService.searchBusinesses(
                keyword, businessType, region, page, size);
        return ResponseData.of(response);
    }

    // ========================================
    // 인증 필요 API
    // ========================================

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     * TODO: rename 고려
     */
    @GetMapping("/my-businesses")
    public ResponseData<BusinessResponseDto.BusinessListResponse> getMyBusinesses(
            @CurrentUserId UUID currentUserId) {

        log.info("내 업체 목록 조회 요청: userId={}", currentUserId);

        BusinessResponseDto.BusinessListResponse response = businessService.getMyBusinesses(currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 사업자용 업체 상세 조회 (민감 정보 포함)
     * 권한: OWNER/MANAGER/MEMBER
     */
    @GetMapping("/{businessId}/profile")
    public ResponseData<BusinessResponseDto.BusinessResponse> getBusinessProfile(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("사업자용 업체 상세 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.getBusinessProfile(
                businessId, currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<BusinessResponseDto.BusinessResponse> createBusiness(
            @Valid @RequestBody BusinessRequestDto.CreateBusinessRequest request,
            @CurrentUserId UUID ownerId) {

        log.info("업체 생성 요청: ownerId={}, businessName={}", ownerId, request.businessName());

        BusinessResponseDto.BusinessResponse response = businessService.createBusiness(request, ownerId);
        return ResponseData.of(response);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @PutMapping("/{businessId}")
    public ResponseData<BusinessResponseDto.BusinessResponse> updateBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.UpdateBusinessRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.updateBusiness(
                businessId, request, currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @DeleteMapping("/{businessId}")
    public ResponseData<BusinessResponseDto.DeleteBusinessResponse> deleteBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.DeleteBusinessRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.DeleteBusinessResponse response = businessService.deleteBusiness(
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
    public ResponseData<BusinessResponseDto.MemberListResponse> getMembersList(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.MemberListResponse response = businessService.getMembersList(
                businessId, currentUserId);
        return ResponseData.of(response);
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     * 변경사항: 신규 초대는 무조건 MEMBER로 생성
     */
    @PostMapping("/{businessId}/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<BusinessResponseDto.MemberListResponse.MemberResponse> inviteMember(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.InviteMemberRequest request,
            @CurrentUserId UUID inviterUserId) {

        log.info("구성원 초대 요청: businessId={}, inviterUserId={}, email={}",
                businessId, inviterUserId, request.email());

        BusinessResponseDto.MemberListResponse.MemberResponse response = businessService.inviteMember(
                businessId, request, inviterUserId);
        return ResponseData.of(response);
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @PatchMapping("/{businessId}/member/{userId}/role")
    public ResponseData<Void> changeMemberRole(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @Valid @RequestBody BusinessRequestDto.ChangeMemberRoleRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterId={}",
                businessId, userId, request.newRole(), currentUserId);

        businessService.changeMemberRole(businessId, userId, request, currentUserId);
        return ResponseData.of(null);
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @DeleteMapping("/{businessId}/member/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseData<Void> removeMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, requesterUserId);

        businessService.removeMember(businessId, userId, requesterUserId);
        return ResponseData.of(null);
    }

    /**
     * 구성원 활성화
     * 권한: OWNER, MANAGER
     */
    @PatchMapping("/{businessId}/member/{userId}/activate")
    public ResponseData<Void> activateMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.activateMember(businessId, userId, currentUserId);
        return ResponseData.of(null);
    }

    /**
     * 구성원 비활성화
     * 권한: OWNER, MANAGER
     */
    @PatchMapping("/{businessId}/member/{userId}/deactivate")
    public ResponseData<Void> deactivateMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 비활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.deactivateMember(businessId, userId, currentUserId);
        return ResponseData.of(null);
    }
}