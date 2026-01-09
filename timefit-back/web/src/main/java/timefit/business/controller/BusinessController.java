package timefit.business.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.service.BusinessService;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.business.*;
import timefit.common.swagger.requestbody.business.*;

import java.util.UUID;

/**
 * Business Controller
 * 권한:
 * - OWNER: 모든 권한
 * - MANAGER: 업체 정보 조회/수정, 구성원 조회/초대
 * - MEMBER: 업체 정보 조회만 가능
 */
@Tag(name = "02. 업체 관리", description = "업체 생성, 조회, 수정, 삭제 및 구성원 관리 API")
@Slf4j
@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // ========================================
    // 공개 API (인증 불필요)
    // ========================================

    @GetBusinessDetailOperation
    @GetMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.PublicBusinessResponse>> getBusinessDetail(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId) {

        log.info("업체 상세 조회 요청: businessId={}", businessId);

        BusinessResponseDto.PublicBusinessResponse response = businessService.getPublicBusinessDetail(businessId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @SearchBusinessesOperation
    @GetMapping("/search")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> searchBusinesses(
            @Parameter(description = "검색 키워드 (업체명, 설명)", example = "미용실")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "업종 코드", example = "BD008")
            @RequestParam(required = false) BusinessTypeCode businessType,
            @Parameter(description = "지역 (주소)", example = "강남구")
            @RequestParam(required = false) String region,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        log.info("업체 검색 요청: keyword={}, businessType={}, region={}, page={}, size={}",
                keyword, businessType, region, page, size);

        BusinessResponseDto.BusinessListResponse response = businessService.searchBusinesses(
                keyword, businessType, region, page, size);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 인증 필요 API
    // ========================================

    @GetMyBusinessesOperation
    @GetMapping("/my-businesses")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> getMyBusinesses(
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("내 업체 목록 조회 요청: userId={}", userId);
        BusinessResponseDto.BusinessListResponse response = businessService.getMyBusinesses(userId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetBusinessProfileOperation
    @GetMapping("/{businessId}/profile")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> getBusinessProfile(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 상세 조회 요청 (사업자용): businessId={}, userId={}", businessId, currentUserId);
        BusinessResponseDto.BusinessResponse response = businessService.getBusinessProfile(businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @CreateBusinessOperation
    @PostMapping
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> createBusiness(
            @CreateBusinessRequestBody
            @Valid @RequestBody BusinessRequestDto.CreateBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID ownerId) {

        log.info("업체 생성 요청: businessName={}, ownerId={}", request.businessName(), ownerId);

        BusinessResponseDto.BusinessResponse response = businessService.createBusiness(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    @UpdateBusinessOperation
    @PutMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> updateBusiness(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @UpdateBusinessRequestBody
            @Valid @RequestBody BusinessRequestDto.UpdateBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);
        BusinessResponseDto.BusinessResponse response = businessService.updateBusiness(businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @DeleteBusinessOperation
    @DeleteMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.DeleteBusinessResponse>> deleteBusiness(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @DeleteBusinessRequestBody
            @Valid @RequestBody BusinessRequestDto.DeleteBusinessRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);
        BusinessResponseDto.DeleteBusinessResponse response = businessService.deleteBusiness(businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @GetMembersListOperation
    @GetMapping("/{businessId}/members")
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse>> getMembersList(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);
        BusinessResponseDto.MemberListResponse response = businessService.getMembersList(businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    @InviteMemberOperation
    @PostMapping("/{businessId}/member")
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse.MemberResponse>> inviteMember(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @InviteMemberRequestBody
            @Valid @RequestBody BusinessRequestDto.InviteMemberRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 초대 요청: businessId={}, email={}, inviterId={}",
                businessId, request.email(), currentUserId);
        BusinessResponseDto.MemberListResponse.MemberResponse response =
                businessService.inviteMember(businessId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    @ChangeMemberRoleOperation
    @PatchMapping("/{businessId}/member/{userId}/role")
    public ResponseEntity<ResponseData<Void>> changeMemberRole(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "대상 사용자 ID", required = true, example = "660e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID userId,
            @ChangeMemberRoleRequestBody
            @Valid @RequestBody BusinessRequestDto.ChangeMemberRoleRequest request,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterId={}",
                businessId, userId, request.newRole(), currentUserId);

        businessService.changeMemberRole(businessId, userId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    @RemoveMemberOperation
    @DeleteMapping("/{businessId}/member/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseData<Void>> removeMember(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "대상 사용자 ID", required = true, example = "660e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, requesterUserId);

        businessService.removeMember(businessId, userId, requesterUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    @ActivateMemberOperation
    @PatchMapping("/{businessId}/member/{userId}/activate")
    public ResponseEntity<ResponseData<Void>> activateMember(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "대상 사용자 ID", required = true, example = "660e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.activateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    @DeactivateMemberOperation
    @PatchMapping("/{businessId}/member/{userId}/deactivate")
    public ResponseEntity<ResponseData<Void>> deactivateMember(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "대상 사용자 ID", required = true, example = "660e8400-e29b-41d4-a716-446655440002")
            @PathVariable UUID userId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 비활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.deactivateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }
}