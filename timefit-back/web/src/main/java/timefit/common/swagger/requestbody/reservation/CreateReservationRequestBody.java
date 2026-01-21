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
                          "businessId": "30000000-0000-0000-0000-000000000001",
                          "menuId": "60000000-0000-0000-0000-000000000001",
                          "bookingSlotId": "50000000-0000-0000-0000-000000000001",
                          "durationMinutes": 60,
                          "totalPrice": 30000,
                          "customerName": "Owner Kim",
                          "customerPhone": "010-1111-1111",
                          "notes": "처음 방문입니다"
                        }
                        """
                        ),
                        @ExampleObject(
                                name = "현장 주문형 서비스 (즉시 예약)",
                                value = """
                        {
                          "businessId": "30000000-0000-0000-0000-000000000001",
                          "menuId": "60000000-0000-0000-0000-000000000001",
                          "reservationDate": "2025-01-10",
                          "reservationTime": "12:00:00",
                          "durationMinutes": 30,
                          "totalPrice": 8000,
                          "customerName": "Owner Kim",
                          "customerPhone": "010-1111-1111"
                        }
                        """
                        )
                }
        )
)
public @interface CreateReservationRequestBody {
}