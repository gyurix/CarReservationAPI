package gyurix.carreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO representing a reservation")
public class ReservationDTO {
    @NotNull
    @Schema(description = "Car ID", example = "C123")
    private String carId;

    @NotNull
    @Schema(description = "End time of the reservation")
    private LocalDateTime endTime;

    @NotNull
    @Schema(description = "Reservation ID")
    private Integer id;

    @NotNull
    @Schema(description = "Start time of the reservation")
    private LocalDateTime startTime;

    @NotNull
    @Schema(description = "User ID")
    private Integer userId;
}
