package timefit.business.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.CustomerSortType;
import timefit.reservation.service.ReservationService;

import java.util.UUID;

/**
 * 업체-고객 관리 Controller
 * - 업체 소속 방문 고객 목록 조회 (COMPLETED 예약 기반 집계)
 * - 방문 고객 상세 조회 (예약 이력 포함)
 * 참고:
 * - 데이터 출처는 reservation 이나 URL(/api/business/.../customers) 기준으로 business 패키지에 위치
 * - 고객용 API(/api/customer/...)와 구분됨
 */
@Tag(name = "02-1. 업체 고객 관리", description = "업체 방문 고객 목록·상세 조회 API")
@Slf4j
@RestController
@RequestMapping("/api/business/{businessId}/customers")
@RequiredArgsConstructor
public class BusinessCustomerController {

    private final ReservationService reservationService;

    /**
     * 방문 고객 목록 조회
     * - COMPLETED 예약 기반 집계
     * - 이름/전화번호 검색, 정렬, 페이지네이션 지원
     */
    @GetMapping
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerList>> getCustomerList(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "이름 또는 전화번호 검색", example = "홍길동")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "정렬 기준 (LAST_VISIT | FIRST_VISIT | TOTAL_VISITS | NAME)", example = "LAST_VISIT")
            @RequestParam(required = false) CustomerSortType sortBy,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 고객 목록 조회 요청: businessId={}, keyword={}, sortBy={}, userId={}",
                businessId, keyword, sortBy, currentUserId);

        ReservationResponseDto.CustomerList response =
                reservationService.getCustomerList(businessId, keyword, sortBy, page, size, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 방문 고객 상세 조회
     * - 고객 기본 정보 + COMPLETED 예약 이력 최신 10건
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseData<ReservationResponseDto.CustomerDetail>> getCustomerDetail(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "고객 ID", required = true, example = "20000000-0000-0000-0000-000000000001")
            @PathVariable UUID customerId,
            @Parameter(hidden = true)
            @CurrentUserId UUID currentUserId) {

        log.info("업체 고객 상세 조회 요청: businessId={}, customerId={}, userId={}",
                businessId, customerId, currentUserId);

        ReservationResponseDto.CustomerDetail response =
                reservationService.getCustomerDetail(businessId, customerId, currentUserId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}