package timefit.common.swagger.requestbody.reservation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.reservation.dto.ReservationRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "예약 취소 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationRequestDto.CancelReservation.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "reason": "개인 사정으로 취소합니다"
                    }
                    """
                )
        )
)
public @interface CancelReservationRequestBody {
}