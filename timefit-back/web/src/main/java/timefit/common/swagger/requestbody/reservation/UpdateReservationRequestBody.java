package timefit.common.swagger.requestbody.reservation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.reservation.dto.ReservationRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "예약 수정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationRequestDto.UpdateReservation.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "reservationDate": "2025-12-02",
                      "reservationTime": "15:00:00",
                      "customerPhone": "01087654321",
                      "notes": "시간 변경 요청합니다",
                      "reason": "개인 사정으로 시간 변경"
                    }
                    """
                )
        )
)
public @interface UpdateReservationRequestBody {
}