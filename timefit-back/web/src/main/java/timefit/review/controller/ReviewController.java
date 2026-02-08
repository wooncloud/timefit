package timefit.review.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timefit.common.ResponseData;
import timefit.common.auth.CurrentUserId;
import timefit.common.swagger.operation.review.*;
import timefit.common.swagger.requestbody.review.*;
import timefit.review.dto.ReviewRequestDto;
import timefit.review.dto.ReviewResponseDto;
import timefit.review.service.ReviewService;

import java.util.UUID;

/**
 * Review Controller
 *
 * 고객용 API:
 * - 리뷰 작성, 수정, 삭제
 * - 내 리뷰 목록 조회
 *
 * 공개 API:
 * - 업체별 리뷰 목록 조회
 * - 리뷰 통계 조회
 */
@Tag(name = "11. 리뷰 관리", description = "고객 리뷰 작성 및 업체 리뷰 조회 API")
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ========== 고객용 API ==========

    /**
     * 리뷰 작성
     * POST /api/customer/review
     */
    @CreateReviewOperation
    @PostMapping("/customer/review")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseData<ReviewResponseDto.ReviewDetail>> createReview(
            @CreateReviewBody
            @Valid @RequestBody ReviewRequestDto.CreateReview request,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("리뷰 작성 요청: userId={}, reservationId={}", userId, request.reservationId());

        ReviewResponseDto.ReviewDetail response = reviewService.createReview(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(response));
    }

    /**
     * 리뷰 수정
     * PUT /api/customer/review/{reviewId}
     */
    @UpdateReviewOperation
    @PutMapping("/customer/review/{reviewId}")
    public ResponseEntity<ResponseData<ReviewResponseDto.ReviewDetail>> updateReview(
            @Parameter(description = "리뷰 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID reviewId,
            @UpdateReviewBody
            @Valid @RequestBody ReviewRequestDto.UpdateReview request,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("리뷰 수정 요청: userId={}, reviewId={}", userId, reviewId);

        ReviewResponseDto.ReviewDetail response = reviewService.updateReview(userId, reviewId, request);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 리뷰 삭제
     * DELETE /api/customer/review/{reviewId}
     */
    @DeleteReviewOperation
    @DeleteMapping("/customer/review/{reviewId}")
    public ResponseEntity<ResponseData<Void>> deleteReview(
            @Parameter(description = "리뷰 ID", required = true, example = "10000000-0000-0000-0000-000000000001")
            @PathVariable UUID reviewId,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("리뷰 삭제 요청: userId={}, reviewId={}", userId, reviewId);

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 내 리뷰 목록 조회
     * GET /api/customer/review/my?page=0&size=20
     */
    @GetMyReviewsOperation
    @GetMapping("/customer/review/my")
    public ResponseEntity<ResponseData<ReviewResponseDto.MyReviewList>> getMyReviews(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true)
            @CurrentUserId UUID userId) {

        log.info("내 리뷰 목록 조회 요청: userId={}, page={}, size={}", userId, page, size);

        ReviewResponseDto.MyReviewList response = reviewService.getMyReviews(userId, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    // ========== 공개 API ==========

    /**
     * 업체별 리뷰 목록 조회 (공개)
     * GET /api/public/business/{businessId}/reviews?minRating=4&page=0&size=20
     */
    @GetBusinessReviewsOperation
    @GetMapping("/public/business/{businessId}/reviews")
    public ResponseEntity<ResponseData<ReviewResponseDto.ReviewList>> getBusinessReviews(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId,
            @Parameter(description = "최소 평점 필터 (1~5)", example = "4")
            @RequestParam(required = false) Integer minRating,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        log.info("업체 리뷰 목록 조회 요청: businessId={}, minRating={}, page={}, size={}",
                businessId, minRating, page, size);

        ReviewResponseDto.ReviewList response =
                reviewService.getBusinessReviews(businessId, minRating, page, size);

        return ResponseEntity.ok(ResponseData.of(response));
    }

    /**
     * 리뷰 통계 조회 (공개)
     * GET /api/public/business/{businessId}/reviews/statistics
     */
    @GetReviewStatisticsOperation
    @GetMapping("/public/business/{businessId}/reviews/statistics")
    public ResponseEntity<ResponseData<ReviewResponseDto.ReviewStatistics>> getReviewStatistics(
            @Parameter(description = "업체 ID", required = true, example = "30000000-0000-0000-0000-000000000001")
            @PathVariable UUID businessId) {

        log.debug("리뷰 통계 조회 요청: businessId={}", businessId);

        ReviewResponseDto.ReviewStatistics response = reviewService.getReviewStatistics(businessId);

        return ResponseEntity.ok(ResponseData.of(response));
    }
}