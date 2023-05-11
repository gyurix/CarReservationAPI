package gyurix.carreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    @NotNull
    @Positive
    @Max(120)
    @Schema(description = "Duration of the reservation in minutes", example = "60")
    private Integer durationInMinutes;

    @NotNull
    @Schema(description = "Start time of the reservation", example = "2023-05-12T10:00:00")
    private LocalDateTime startTime;

    @NotNull
    @Positive
    @Schema(description = "User ID associated with the reservation", example = "12345")
    private Integer userId;
}
