package gyurix.carreservation.controllers;

import gyurix.carreservation.dto.ReservationDTO;
import gyurix.carreservation.dto.ReservationRequest;
import gyurix.carreservation.services.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Controller")
@Validated
public class ReservationController {
    private final ReservationService reservationService;

    @Operation(summary = "Get all reservations")
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get a reservation by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the reservation"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Integer id) {
        ReservationDTO reservationDTO = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationDTO);
    }

    @Operation(summary = "Reserve a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car reserved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reservation parameters"),
            @ApiResponse(responseCode = "422", description = "No available cars for the specified time and duration"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ReservationDTO> reserveCar(@Valid @RequestBody ReservationRequest reservationRequest) {
        ReservationDTO reservationDTO = reservationService.reserveCar(reservationRequest.getStartTime(),
                reservationRequest.getDurationInMinutes(), reservationRequest.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDTO);
    }
}
