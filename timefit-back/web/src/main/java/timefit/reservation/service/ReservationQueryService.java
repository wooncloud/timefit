package timefit.reservation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationResponseDto;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.factory.ReservationResponseFactory;
import timefit.reservation.repository.ReservationRepositoryCustom;
import timefit.reservation.service.util.ReservationValidationUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

/**
 * 예약 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepositoryCustom reservationRepositoryCustom;
    private final ReservationResponseFactory reservationResponseFactory;
    private final ReservationValidationUtil validationUtil;

    /**
     * 내 예약 목록 조회 (기존 Repository 활용)
     */
    public ResponseData<ReservationResponseDto.ReservationListResult> getMyReservations(
            UUID customerId, String status, String startDate, String endDate, UUID businessId, int page, int size) {

        log.info("내 예약 목록 조회 시작: customerId={}, status={}", customerId, status);

        // 1. 페이징 검증
        validationUtil.validatePagingParameters(page, size);

        // 2. 필터 파라미터 파싱
        LocalDate startLocalDate = validationUtil.parseDate(startDate);
        LocalDate endLocalDate = validationUtil.parseDate(endDate);
        ReservationStatus statusFilter = validationUtil.parseStatus(status);

        // 3. 페이징 설정
        PageRequest pageRequest = PageRequest.of(page, size,
                Sort.by("reservationDate").descending()
                        .and(Sort.by("reservationTime").descending()));

        // 4. 기존 Repository 메서드 활용
        Page<Reservation> reservationPage = reservationRepositoryCustom.findMyReservationsWithFilters(
                customerId, statusFilter, startLocalDate, endLocalDate, businessId, pageRequest);

        // 5. 결과 처리
        if (reservationPage.isEmpty()) {
            log.info("내 예약 목록 조회 결과 없음: customerId={}", customerId);
            return ResponseData.of(this.createEmptyResult(page, size));
        }

        // 6. 응답 생성
        ReservationResponseDto.ReservationListResult response =
                reservationResponseFactory.createReservationListResponse(reservationPage);

        log.info("내 예약 목록 조회 완료: customerId={}, totalElements={}",
                customerId, reservationPage.getTotalElements());

        return ResponseData.of(response);
    }

    /**
     * 빈 결과 생성
     */
    private ReservationResponseDto.ReservationListResult createEmptyResult(int page, int size) {
        ReservationResponseDto.PaginationInfo pagination = ReservationResponseDto.PaginationInfo.of(
                page, 0, 0L, size, false, false);
        return ReservationResponseDto.ReservationListResult.of(Collections.emptyList(), pagination);
    }
}