package gyurix.carreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "DTO representing a car")
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {
    @NotNull
    @Pattern(regexp = "^C\\d+")
    @Schema(description = "Car ID", example = "C123")
    private String id;

    @NotNull
    @Schema(description = "Car make", example = "Toyota")
    private String make;

    @NotNull
    @Schema(description = "Car model", example = "Camry")
    private String model;
}
