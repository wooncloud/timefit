package timefit.common.swagger.operation.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import timefit.common.ResponseData;
import timefit.reservation.dto.ReservationResponseDto;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "예약 생성 (고객용)",
        description = """
            새로운 예약을 생성합니다.
            
            1. Request Body 필수값
               - businessId: 업체 ID
               - menuId: 메뉴 ID
               - durationMinutes: 서비스 시간
               - totalPrice: 예약 금액
               - customerName: 예약자 이름
               - customerPhone: 연락처
            
            2. Request Body 선택값 (예약 유형에 따라)
               - bookingSlotId: 슬롯 ID (RESERVATION_BASED일 때 필수)
               - reservationDate: 예약 날짜 (ONDEMAND_BASED일 때 필수)
               - reservationTime: 예약 시간 (ONDEMAND_BASED일 때 필수)
               - notes: 메모
            
            3. 예약 유형
               - RESERVATION_BASED: bookingSlotId 제공
               - ONDEMAND_BASED: reservationDate, reservationTime 제공
            
            4. 제약사항
               - durationMinutes: 10~480분
               - customerPhone: 10~11자리 숫자
               - notes: 최대 500자
            
            5. 권한
               - 로그인 필요 (CUSTOMER)
            """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ReservationResponseDto.CustomerReservation.class),
                        examples = @ExampleObject(
                                value = """
                        {
                          "success": true,
                          "data": {
                            "reservationId": "550e8400-e29b-41d4-a716-446655440000",
                            "reservationNumber": "R20251123001",
                            "status": "PENDING",
                            "businessName": "강남 헤어샵",
                            "menuServiceName": "헤어 컷",
                            "reservationDate": "2025-12-01",
                            "reservationTime": "14:00:00",
                            "reservationPrice": 30000
                          }
                        }
                        """
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = """
                VALIDATION_ERROR - 요청 형식 오류
                1. businessId 은(는) 필수 값입니다.
                2. menuId 은(는) 필수 값입니다.
                3. durationMinutes 은(는) 필수 값입니다.
                4. totalPrice 은(는) 필수 값입니다.
                5. customerName 은(는) 필수 값입니다.
                6. customerPhone 은(는) 필수 값입니다.
                
                SLOT_NOT_AVAILABLE - 슬롯 예약 불가
                
                MENU_NOT_ACTIVE - 비활성화된 메뉴
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = """
                BUSINESS_NOT_FOUND - 업체를 찾을 수 없음
                
                MENU_NOT_FOUND - 메뉴를 찾을 수 없음
                
                BOOKING_SLOT_NOT_FOUND - 슬롯을 찾을 수 없음
                """,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class)
                )
        )
})
public @interface CreateReservationOperation {
}