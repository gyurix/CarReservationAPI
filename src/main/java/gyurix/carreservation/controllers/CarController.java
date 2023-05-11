package gyurix.carreservation.controllers;

import gyurix.carreservation.dto.CarDTO;
import gyurix.carreservation.services.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Tag(name = "Car Controller")
@Validated
public class CarController {

    private final CarService carService;

    @PostMapping
    @Operation(summary = "Add a new car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Car added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Car already exists")
    })
    public ResponseEntity<Void> addCar(@Valid @RequestBody CarDTO carDTO) {
        carService.addCar(carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a car by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    public ResponseEntity<Void> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping
    @Operation(summary = "Get all cars")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CarDTO.class)))
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a car by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CarDTO.class))),
            @ApiResponse(responseCode = "404", description = "Car not found")
    })
    public ResponseEntity<CarDTO> getCarById(@PathVariable String id) {
        CarDTO carDTO = carService.getCarById(id);
        return ResponseEntity.ok(carDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a car by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Car not found"),
            @ApiResponse(responseCode = "409", description = "Car unchanged")
    })
    public ResponseEntity<Void> updateCar(@PathVariable String id, @Valid @RequestBody CarDTO carDTO) {
        carDTO.setId(id);
        carService.updateCar(carDTO);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}