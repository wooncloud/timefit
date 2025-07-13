package timefit.business.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import timefit.business.dto.BusinessRequestDto;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.service.BusinessService;
import timefit.common.ResponseData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
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

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인 (모든 권한)
     * + 근데 "나" 의 비즈니스 리스트를 보여주는거라 추가 url을 달아야될 것 같기도?
     */
    @GetMapping
    public ResponseData<List<BusinessResponseDto.BusinessSummary>> getMyBusinesses(HttpServletRequest request) {
        UUID currentUserId = getCurrentUserId(request);

        log.info("내 업체 목록 조회 요청: userId={}", currentUserId);

        return businessService.getMyBusinesses(currentUserId);
    }

    /**
     * 업체 생성
     * 권한: 로그인한 사용자 누구나 (생성자는 자동으로 OWNER가 됨)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<BusinessResponseDto.BusinessDetail> createBusiness(
            @Valid @RequestBody BusinessRequestDto.CreateBusiness request,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("업체 생성 요청: userId={}, businessName={}", currentUserId, request.getBusinessName());

        return businessService.createBusiness(request, currentUserId);
    }

    /**
     * 업체 상세 정보 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    @GetMapping("/{businessId}")
    public ResponseData<BusinessResponseDto.BusinessDetail> getBusinessDetail (
            @PathVariable UUID businessId,  HttpServletRequest request) {

        UUID currentUserId = getCurrentUserId(request);
        log.info("업체 상세 조회 요청: businessId={}, userId={}", businessId, currentUserId);
        return businessService.getBusinessDetail(businessId, currentUserId);
    }

    /**
     * 업체 정보 수정
     * 권한: OWNER, MANAGER만 가능
     */
    @PutMapping("/{businessId}")
    public ResponseData<BusinessResponseDto.BusinessProfile> updateBusiness (
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.UpdateBusiness request,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("업체 정보 수정 요청: businessId={}, userId={}", businessId, currentUserId);

        return businessService.updateBusiness(businessId, request, currentUserId);
    }


    /**
     * 업체 삭제 (비활성화)
     * 권한: OWNER만 가능
     */
    @DeleteMapping("/{businessId}")
    public ResponseData<BusinessResponseDto.DeleteResult> deleteBusiness(
            @PathVariable UUID businessId,
            @Valid @RequestBody BusinessRequestDto.DeleteBusiness request,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("업체 삭제 요청: businessId={}, userId={}", businessId, currentUserId);

        return businessService.deleteBusiness(businessId, request, currentUserId);
    }

    /**
     * 업체 활성화/비활성화 토글
     * 권한: OWNER만 가능
     * 특징: 단순 상태 변경 (성공/실패만 반환)
     */
    @PostMapping("/{businessId}/toggle-status")
    public ResponseData<String> toggleBusinessStatus(
            @PathVariable UUID businessId,
            HttpServletRequest httpRequest) {
        UUID currentUserId = getCurrentUserId(httpRequest);

        log.info("업체 상태 토글 요청: businessId={}, userId={}", businessId, currentUserId);

        return businessService.toggleBusinessStatus(businessId, currentUserId);
    }

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER만 가능
     */

    /**
     * 구성원 초대
     * 권한: OWNER, MANAGER만 가능
     */

    /**
     * 구성원 권한 변경
     * 권한: OWNER만 가능
     */

    /**
     * 구성원 제거
     * 권한: OWNER만 가능
     */


    /**
     * 업체 검색 (공개 API - 인증 불필요)
     * 권한: 모든 사용자 (로그인 불필요)
     */





    /**
     * 현재 사용자 ID 추출 (AuthFilter 에서 설정)
     */
    private UUID getCurrentUserId(HttpServletRequest request) {
        // AuthFilter 에서 request.setAttribute("userId", userId) 설정
        Object userId = request.getAttribute("userId");

        if (userId == null) {
            throw new RuntimeException("사용자 인증 정보가 없습니다. AuthFilter 확인 필요");
        }

        return (UUID) userId;
    }
}