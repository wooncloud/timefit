package timefit.common.swagger.requestbody.booking;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.booking.dto.BookingSlotRequest;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "슬롯 생성 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BookingSlotRequest.BookingSlot.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "menuId": "60000000-0000-0000-0000-000000000001",
                      "slotIntervalMinutes": 30,
                      "schedules": [
                        {
                          "date": "2025-01-10",
                          "timeRanges": [
                            {
                              "startTime": "09:00",
                              "endTime": "12:00"
                            },
                            {
                              "startTime": "13:00",
                              "endTime": "18:00"
                            }
                          ]
                        },
                        {
                          "date": "2025-01-11",
                          "timeRanges": []
                        }
                      ]
                    }
                    """
                )
        )
)
public @interface CreateSlotsRequestBody {
}