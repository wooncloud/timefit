package timefit.common.swagger.requestbody.operatinghours;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import timefit.operatinghours.dto.OperatingHoursRequestDto;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "영업시간 설정 요청",
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OperatingHoursRequestDto.SetOperatingHours.class),
                examples = @ExampleObject(
                        value = """
                    {
                      "schedules": [
                        {
                          "dayOfWeek": 0,
                          "openTime": null,
                          "closeTime": null,
                          "isClosed": true,
                          "bookingTimeRanges": []
                        },
                        {
                          "dayOfWeek": 1,
                          "openTime": "09:00",
                          "closeTime": "18:00",
                          "isClosed": false,
                          "bookingTimeRanges": [
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
                          "dayOfWeek": 2,
                          "openTime": "09:00",
                          "closeTime": "18:00",
                          "isClosed": false,
                          "bookingTimeRanges": [
                            {
                              "startTime": "09:00",
                              "endTime": "18:00"
                            }
                          ]
                        },
                        {
                          "dayOfWeek": 3,
                          "openTime": "09:00",
                          "closeTime": "18:00",
                          "isClosed": false,
                          "bookingTimeRanges": [
                            {
                              "startTime": "09:00",
                              "endTime": "18:00"
                            }
                          ]
                        },
                        {
                          "dayOfWeek": 4,
                          "openTime": "09:00",
                          "closeTime": "18:00",
                          "isClosed": false,
                          "bookingTimeRanges": [
                            {
                              "startTime": "09:00",
                              "endTime": "18:00"
                            }
                          ]
                        },
                        {
                          "dayOfWeek": 5,
                          "openTime": "09:00",
                          "closeTime": "18:00",
                          "isClosed": false,
                          "bookingTimeRanges": [
                            {
                              "startTime": "09:00",
                              "endTime": "18:00"
                            }
                          ]
                        },
                        {
                          "dayOfWeek": 6,
                          "openTime": null,
                          "closeTime": null,
                          "isClosed": true,
                          "bookingTimeRanges": []
                        }
                      ]
                    }
                    """
                )
        )
)
public @interface SetOperatingHoursRequestBody {
}