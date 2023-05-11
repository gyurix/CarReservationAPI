package gyurix.carreservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "DTO representing an error response")
public class ErrorResponseDTO {
    @Schema(description = "Status code and status message", example = "400 - Bad Request")
    private String status;

    @Schema(description = "Error message", example = "Validation error")
    private String message;

    @Schema(description = "List of error details")
    private List<String> errors;
}