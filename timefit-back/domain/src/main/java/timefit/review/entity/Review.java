package timefit.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import timefit.business.entity.Business;
import timefit.common.entity.BaseEntity;
import timefit.reservation.entity.Reservation;
import timefit.user.entity.User;

import java.time.LocalDateTime;

/**
 * 리뷰(Review) 엔티티
 *
 * 핵심 기능:
 * - 예약 완료 후 고객이 작성하는 리뷰
 * - 1개 예약당 1개 리뷰만 작성 가능 (reservationId UNIQUE)
 * - Soft Delete 방식 (deletedAt 필드 사용)
 * - 리뷰 작성/수정/삭제 시 Business의 평점 자동 갱신
 *
 * 비즈니스 규칙:
 * - 예약 상태가 COMPLETED일 때만 리뷰 작성 가능
 * - 평점은 1~5 사이 정수
 * - menuName은 스냅샷 (Menu 삭제되어도 표시)
 * - 삭제 시 실제 삭제하지 않고 deletedAt 업데이트
 */
@Entity
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_business_created", columnList = "business_id, created_at DESC"),
                @Index(name = "idx_review_business_rating", columnList = "business_id, rating"),
                @Index(name = "idx_review_user_created", columnList = "user_id, created_at DESC"),
                @Index(name = "idx_review_deleted", columnList = "deleted_at")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "unique_reservation_review",
                columnNames = {"reservation_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    // 리뷰가 작성된 업체
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Business business;

    // 리뷰 작성자
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 연결된 예약
     * 1개 예약당 1개 리뷰만 작성 가능 (UNIQUE 제약)
     * null 가능 (예약 없이도 리뷰 작성 가능하도록 확장 고려)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Reservation reservation;

    /**
     * 서비스명 스냅샷
     * Menu가 삭제되어도 리뷰에 서비스명 표시
     */
    @NotNull
    @Size(max = 100, message = "서비스명은 100자 이하로 입력해주세요")
    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    // 평점 (1~5)
    @NotNull
    @Min(value = 1, message = "평점은 최소 1점입니다")
    @Max(value = 5, message = "평점은 최대 5점입니다")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    // 리뷰 내용
    @Size(max = 1000, message = "리뷰는 1000자 이하로 입력해주세요")
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * 삭제 일시 (Soft Delete)
     * null이면 활성 상태
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ---------------------- 정적 팩토리 메서드

    /**
     * Review 생성
     *
     * @param business 업체
     * @param user 작성자
     * @param reservation 예약 (nullable)
     * @param menuName 서비스명 스냅샷
     * @param rating 평점 (1~5)
     * @param comment 리뷰 내용
     * @return 생성된 Review 엔티티
     */
    public static Review create(
            Business business,
            User user,
            Reservation reservation,
            String menuName,
            Integer rating,
            String comment) {

        validateCreateFields(business, user, menuName, rating);

        Review review = new Review();
        review.business = business;
        review.user = user;
        review.reservation = reservation;
        review.menuName = menuName;
        review.rating = rating;
        review.comment = comment;
        review.deletedAt = null;
        return review;
    }

    // ---------------------- 비즈니스 메서드

    /**
     * 리뷰 수정
     *
     * @param rating 새 평점
     * @param comment 새 리뷰 내용
     */
    public void update(Integer rating, String comment) {
        validateRating(rating);

        this.rating = rating;
        this.comment = comment;
    }

    // 리뷰 삭제 (Soft Delete)
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인
     *
     * @return 삭제된 리뷰인지 여부
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // ---------------------- 검증 메서드

    // 리뷰 생성 시 필수 필드 검증
    private static void validateCreateFields(
            Business business,
            User user,
            String menuName,
            Integer rating) {

        if (business == null) {
            throw new IllegalArgumentException("업체는 필수입니다");
        }
        if (user == null) {
            throw new IllegalArgumentException("사용자는 필수입니다");
        }
        if (menuName == null || menuName.isBlank()) {
            throw new IllegalArgumentException("서비스명은 필수입니다");
        }
        validateRating(rating);
    }

    // 평점 범위 검증
    private static void validateRating(Integer rating) {
        if (rating == null) {
            throw new IllegalArgumentException("평점은 필수입니다");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1~5 사이여야 합니다");
        }
    }
}