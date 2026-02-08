package timefit.review.service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.exception.review.ReviewErrorCode;
import timefit.exception.review.ReviewException;
import timefit.reservation.entity.Reservation;
import timefit.reservation.entity.ReservationStatus;
import timefit.reservation.service.validator.ReservationValidator;
import timefit.review.entity.Review;
import timefit.review.repository.ReviewRepository;

import java.util.UUID;

/**
 * Review 검증 전담 클래스
 *
 * 주요 역할:
 * - Review 존재 여부 검증
 * - 리뷰 작성 가능 여부 검증 (예약 상태)
 * - 리뷰 중복 검증
 * - 소유권 검증
 * - 삭제 상태 검증
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewRepository reviewRepository;
    private final ReservationValidator reservationValidator;

    /**
     * Review 존재 여부 검증 및 조회 (Soft Delete 제외)
     *
     * @param reviewId Review ID
     * @return 조회된 Review 엔티티
     * @throws ReviewException 존재하지 않거나 삭제된 경우
     */
    public Review validateExists(UUID reviewId) {
        return reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 리뷰 ID: {}", reviewId);
                    return new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND);
                });
    }

    /**
     * 리뷰 작성 가능 여부 검증
     *
     * 조건:
     * 1. 예약 상태가 COMPLETED여야 함
     * 2. 예약자가 본인이어야 함
     * 3. 이미 리뷰를 작성하지 않았어야 함
     *
     * @param reservationId 예약 ID
     * @param userId 사용자 ID
     * @return 검증된 Reservation 엔티티
     * @throws ReviewException 검증 실패 시
     */
    public Reservation validateCanWriteReview(UUID reservationId, UUID userId) {
        log.debug("리뷰 작성 가능 여부 검증 시작: reservationId={}, userId={}", reservationId, userId);

        // 1. 예약 존재 확인
        Reservation reservation = reservationValidator.validateExists(reservationId);

        // 2. 예약 소유권 확인
        if (!reservation.getCustomer().getId().equals(userId)) {
            log.warn("예약 소유자 불일치: reservationId={}, userId={}, actualUserId={}",
                    reservationId, userId, reservation.getCustomer().getId());
            throw new ReviewException(ReviewErrorCode.RESERVATION_OWNER_MISMATCH);
        }

        // 3. 예약 상태 확인 (COMPLETED만 가능)
        if (!ReservationStatus.COMPLETED.equals(reservation.getStatus())) {
            log.warn("완료되지 않은 예약에 리뷰 작성 시도: reservationId={}, status={}",
                    reservationId, reservation.getStatus());
            throw new ReviewException(ReviewErrorCode.INVALID_RESERVATION_STATUS);
        }

        // 4. 중복 리뷰 확인
        if (reviewRepository.existsByReservationId(reservationId)) {
            log.warn("중복 리뷰 작성 시도: reservationId={}", reservationId);
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        log.debug("리뷰 작성 가능 여부 검증 완료: reservationId={}", reservationId);
        return reservation;
    }

    /**
     * Review 소유권 검증
     * 해당 Review가 사용자 소유인지 확인
     *
     * @param review 검증할 Review
     * @param userId 사용자 ID
     * @throws ReviewException 소유권이 없는 경우
     */
    public void validateOwnership(Review review, UUID userId) {
        if (!review.getUser().getId().equals(userId)) {
            log.warn("리뷰 소유권 없음: reviewId={}, userId={}, actualUserId={}",
                    review.getId(), userId, review.getUser().getId());
            throw new ReviewException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }
    }

    /**
     * Review가 삭제되지 않았는지 확인
     *
     * @param review 검증할 Review
     * @throws ReviewException 이미 삭제된 경우
     */
    public void validateNotDeleted(Review review) {
        if (review.isDeleted()) {
            log.warn("삭제된 리뷰에 접근 시도: reviewId={}", review.getId());
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_DELETED);
        }
    }

    /**
     * Business 소유자/관리자가 리뷰를 볼 권한이 있는지 확인
     *
     * @param review 검증할 Review
     * @param businessId 업체 ID
     * @throws ReviewException 업체에 속하지 않는 리뷰인 경우
     */
    public void validateBusinessAccess(Review review, UUID businessId) {
        if (!review.getBusiness().getId().equals(businessId)) {
            log.warn("업체에 속하지 않는 리뷰: reviewId={}, businessId={}, actualBusinessId={}",
                    review.getId(), businessId, review.getBusiness().getId());
            throw new ReviewException(ReviewErrorCode.REVIEW_ACCESS_DENIED);
        }
    }
}