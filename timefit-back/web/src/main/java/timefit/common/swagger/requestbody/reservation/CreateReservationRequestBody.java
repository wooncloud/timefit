package timefit.common.swagger.requestbody.reservation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.reservation.dto.ReservationRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "예약 생성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationRequestDto.CreateReservation.class),
                examples = {
                        @ExampleObject(
                                name = "예약형 서비스 (슬롯 기반)",
                                value = """
                        {
                          "businessId": "550e8400-e29b-41d4-a716-446655440001",
                          "menuId": "550e8400-e29b-41d4-a716-446655440002",
                          "bookingSlotId": "550e8400-e29b-41d4-a716-446655440003",
                          "durationMinutes": 60,
                          "totalPrice": 30000,
                          "customerName": "홍길동",
                          "customerPhone": "01012345678",
                          "notes": "처음 방문입니다"
                        }
                        """
                        ),
                        @ExampleObject(
                                name = "현장 주문형 서비스 (즉시 예약)",
                                value = """
                        {
                          "businessId": "550e8400-e29b-41d4-a716-446655440001",
                          "menuId": "550e8400-e29b-41d4-a716-446655440002",
                          "reservationDate": "2025-12-01",
                          "reservationTime": "12:00:00",
                          "durationMinutes": 30,
                          "totalPrice": 8000,
                          "customerName": "홍길동",
                          "customerPhone": "01012345678"
                        }
                        """
                        )
                }
        )
)
public @interface CreateReservationRequestBody {
}