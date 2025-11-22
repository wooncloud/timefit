package timefit.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "업체 관리", description = "업체 생성, 조회, 수정, 삭제 및 구성원 관리 API")
public class BusinessController {

    private final BusinessService businessService;

    // ========================================
    // 공개 API (인증 불필요)
    // ========================================

    /**
     * 업체 상세 정보 조회 (공개)
     * 권한: 누구나 조회 가능
     */
    @Operation(summary = "업체 공개 상세 조회", description = "인증 없이 업체의 기본 정보를 조회합니다. (업체명, 주소, 연락처, 업종 등)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.PublicBusinessResponse.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.PublicBusinessResponse>> getBusinessDetail(
            @PathVariable UUID businessId) {

        log.info("업체 상세 조회 요청: businessId={}", businessId);

        BusinessResponseDto.PublicBusinessResponse response = businessService.getPublicBusinessDetail(businessId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    @Operation(summary = "업체 검색 (페이징)", description = "키워드, 업종, 지역을 조건으로 업체를 검색합니다. 페이징을 지원합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.BusinessListResponse.class))),
            @ApiResponse(responseCode = "400", description = "페이지 번호 또는 크기가 올바르지 않습니다 (INVALID_PAGE_NUMBER, INVALID_PAGE_SIZE)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> searchBusinesses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BusinessTypeCode businessType,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
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

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
    @Operation(summary = "내 업체 목록 조회", description = "현재 로그인한 사용자가 소속된 모든 업체 목록을 조회합니다. (권한 정보 포함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.BusinessListResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (@CurrentUserId 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/my-businesses")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessListResponse>> getMyBusinesses(
            @CurrentUserId UUID currentUserId) {

        log.info("내 업체 목록 조회 요청: userId={}", currentUserId);

        BusinessResponseDto.BusinessListResponse response = businessService.getMyBusinesses(currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 사업자용 업체 상세 조회 (민감 정보 포함)
     * 권한: OWNER/MANAGER/MEMBER
     */
    @Operation(summary = "업체 상세 조회 (사업자용)", description = "업체 구성원만 조회 가능한 상세 정보를 포함합니다. (사업자번호, 내부 공지사항, 내 권한 등)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 업체에 소속된 구성원이 아닙니다 (USER_NOT_BUSINESS_MEMBER, INSUFFICIENT_PERMISSION)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/{businessId}/profile")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> getBusinessProfile(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("사업자용 업체 상세 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.getBusinessProfile(
                businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    @Operation(summary = "업체 생성", description = "새로운 업체를 등록합니다. 생성자는 자동으로 OWNER 권한을 부여받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "업체 생성 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (@Valid)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다 (USER_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자번호입니다 (BUSINESS_ALREADY_EXISTS)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> createBusiness(
            @Valid @RequestBody BusinessRequestDto.CreateBusinessRequest request,
            @CurrentUserId UUID ownerId) {

        log.info("업체 생성 요청: ownerId={}, businessName={}", ownerId, request.businessName());

        BusinessResponseDto.BusinessResponse response = businessService.createBusiness(request, ownerId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @Operation(summary = "업체 정보 수정", description = "업체의 기본 정보를 수정합니다. OWNER 또는 MANAGER 권한이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.BusinessResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (@Valid)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER/MANAGER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "409", description = "이미 등록된 사업자번호입니다 (BUSINESS_ALREADY_EXISTS - 사업자번호 변경 시)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PutMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.BusinessResponse>> updateBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.UpdateBusinessRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.BusinessResponse response = businessService.updateBusiness(
                businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @Operation(summary = "업체 삭제 (비활성화)", description = "업체를 비활성화합니다. 실제 데이터는 삭제되지 않으며, OWNER만 실행할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.DeleteBusinessResponse.class))),
            @ApiResponse(responseCode = "400", description = "삭제 확인이 필요합니다 (DELETE_CONFIRMATION_REQUIRED) 또는 이미 삭제된 업체입니다 (BUSINESS_ALREADY_DELETED)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @DeleteMapping("/{businessId}")
    public ResponseEntity<ResponseData<BusinessResponseDto.DeleteBusinessResponse>> deleteBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.DeleteBusinessRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.DeleteBusinessResponse response = businessService.deleteBusiness(
                businessId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========================================
    // 구성원 관리 API
    // ========================================

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    @Operation(summary = "업체 구성원 목록 조회", description = "업체에 소속된 모든 구성원의 정보와 권한을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.MemberListResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 업체에 소속된 구성원이 아닙니다 (USER_NOT_BUSINESS_MEMBER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND) 또는 활성화된 구성원이 없습니다 (NO_ACTIVE_MEMBERS)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @GetMapping("/{businessId}/members")
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse>> getMembersList(
            @PathVariable UUID businessId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 목록 조회 요청: businessId={}, userId={}", businessId, currentUserId);

        BusinessResponseDto.MemberListResponse response = businessService.getMembersList(
                businessId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER
     * 변경사항: 신규 초대는 무조건 MEMBER로 생성
     */
    @Operation(summary = "업체 구성원 초대", description = "이메일로 새로운 구성원을 초대합니다. 초대된 구성원은 MEMBER 권한으로 시작합니다. (OWNER, MANAGER만 실행 가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "초대 성공",
                    content = @Content(schema = @Schema(implementation = BusinessResponseDto.MemberListResponse.MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 유효성 검사 실패 (@Valid)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER/MANAGER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체 또는 초대할 사용자를 찾을 수 없습니다 (BUSINESS_NOT_FOUND, USER_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "409", description = "이미 업체 구성원입니다 (USER_ALREADY_MEMBER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PostMapping("/{businessId}/member")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<BusinessResponseDto.MemberListResponse.MemberResponse>> inviteMember(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.InviteMemberRequest request,
            @CurrentUserId UUID inviterUserId) {

        log.info("구성원 초대 요청: businessId={}, inviterUserId={}, email={}",
                businessId, inviterUserId, request.email());

        BusinessResponseDto.MemberListResponse.MemberResponse response = businessService.inviteMember(
                businessId, request, inviterUserId);
        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */
    @Operation(summary = "구성원 권한 변경", description = "구성원의 권한을 변경합니다. OWNER만 실행할 수 있으며, OWNER 권한으로는 변경할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
            @ApiResponse(responseCode = "400", description = "본인의 권한은 변경할 수 없습니다 (CANNOT_CHANGE_OWN_ROLE) 또는 OWNER 권한으로 변경할 수 없습니다 (CANNOT_CHANGE_TO_OWNER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체 또는 구성원을 찾을 수 없습니다 (BUSINESS_NOT_FOUND, USER_NOT_MEMBER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PatchMapping("/{businessId}/member/{userId}/role")
    public ResponseEntity<ResponseData<Void>> changeMemberRole(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @Valid @RequestBody BusinessRequestDto.ChangeMemberRoleRequest request,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 권한 변경 요청: businessId={}, targetUserId={}, newRole={}, requesterId={}",
                businessId, userId, request.newRole(), currentUserId);

        businessService.changeMemberRole(businessId, userId, request, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 제거
     * 권한: OWNER는 모든 구성원 제거 가능, MANAGER는 MEMBER만 제거 가능
     */
    @Operation(summary = "구성원 제거 (논리적 삭제)", description = "업체 구성원을 제거합니다. OWNER는 모든 구성원 제거 가능하고, MANAGER는 MEMBER만 제거할 수 있습니다. 본인과 OWNER는 제거할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "제거 성공 (NO_CONTENT)"),
            @ApiResponse(responseCode = "400", description = "본인을 제거할 수 없습니다 (CANNOT_REMOVE_SELF) 또는 OWNER는 제거할 수 없습니다 (CANNOT_REMOVE_OWNER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - MANAGER가 MANAGER 제거 시도)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체 또는 구성원을 찾을 수 없습니다 (BUSINESS_NOT_FOUND, USER_NOT_MEMBER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @DeleteMapping("/{businessId}/member/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseData<Void>> removeMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID requesterUserId) {

        log.info("구성원 제거 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, requesterUserId);

        businessService.removeMember(businessId, userId, requesterUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 활성화
     * 권한: OWNER, MANAGER
     */
    @Operation(summary = "구성원 활성화", description = "비활성화된 구성원을 다시 활성화합니다. OWNER 또는 MANAGER가 실행할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "활성화 성공"),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER/MANAGER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체를 찾을 수 없습니다 (BUSINESS_NOT_FOUND)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PatchMapping("/{businessId}/member/{userId}/activate")
    public ResponseEntity<ResponseData<Void>> activateMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.activateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }

    /**
     * 구성원 비활성화
     * 권한: OWNER, MANAGER
     */
    @Operation(summary = "구성원 비활성화", description = "구성원을 일시적으로 비활성화합니다. OWNER 또는 MANAGER가 실행할 수 있으며, OWNER는 비활성화할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비활성화 성공"),
            @ApiResponse(responseCode = "400", description = "OWNER는 비활성화할 수 없습니다 (CANNOT_REMOVE_OWNER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "403", description = "해당 작업을 수행할 권한이 없습니다 (INSUFFICIENT_PERMISSION - OWNER/MANAGER 아님)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "404", description = "업체 또는 구성원을 찾을 수 없습니다 (BUSINESS_NOT_FOUND, USER_NOT_MEMBER)",
                    content = @Content(schema = @Schema(implementation = ResponseData.class)))
    })
    @PatchMapping("/{businessId}/member/{userId}/deactivate")
    public ResponseEntity<ResponseData<Void>> deactivateMember(
            @PathVariable UUID businessId,
            @PathVariable UUID userId,
            @CurrentUserId UUID currentUserId) {

        log.info("구성원 비활성화 요청: businessId={}, targetUserId={}, requesterId={}",
                businessId, userId, currentUserId);

        businessService.deactivateMember(businessId, userId, currentUserId);
        return ResponseEntity.ok(ResponseData.of(null));
    }
}