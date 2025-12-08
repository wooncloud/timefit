package timefit.common.swagger.operation.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "내 예약 목록 조회 (고객용)",
        description = """
            로그인한 고객의 예약 목록을 조회합니다.
            
            1. Query Parameter (모두 선택)
               - status: 예약 상태 필터
               - startDate: 시작 날짜 (YYYY-MM-DD)
               - endDate: 종료 날짜 (YYYY-MM-DD)
               - businessId: 업체 ID 필터
               - page: 페이지 번호 (0부터 시작, 기본값: 0)
               - size: 페이지 크기 (기본값: 20)
            
            2. 예약 상태
               - PENDING: 대기중
               - CONFIRMED: 확정됨
               - REJECTED: 거절됨
               - CANCELLED: 취소됨
               - COMPLETED: 완료됨
               - NO_SHOW: 노쇼
            
            3. 권한
               - 로그인 필요 (CUSTOMER)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponseDto.CustomerReservationList.class)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "INVALID_PARAMETER - 잘못된 파라미터",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface GetMyReservationsOperation {
}