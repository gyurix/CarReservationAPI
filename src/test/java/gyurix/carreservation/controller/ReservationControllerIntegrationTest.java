package gyurix.carreservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.dto.CarDTO;
import gyurix.carreservation.dto.ReservationDTO;
import gyurix.carreservation.dto.ReservationRequest;
import gyurix.carreservation.services.CarService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ReservationControllerIntegrationTest {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void _setup(@Autowired CarService carService) {
        carService.addCar(new CarDTO("C150", "Toyota", "Camry"));
        carService.addCar(new CarDTO("C450", "Honda", "Accord"));
    }

    private static String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Integer createReservationInDatabase() {
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setStartTime(LocalDateTime.now(Clock.systemUTC()).plusHours(26));
        reservationRequest.setDurationInMinutes(90);
        reservationRequest.setUserId(456);

        try {
            String response = mockMvc.perform(post("/api/v1/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(reservationRequest)))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReservationDTO reservationDTO = objectMapper.readValue(response, ReservationDTO.class);
            System.out.println("Created reservation " + reservationDTO);
            return reservationDTO.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    void getAllReservations_ReturnsAllReservations() throws Exception {
        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getReservationById_ExistingReservationId_ReturnsReservation() throws Exception {
        Integer reservationId = createReservationInDatabase();

        mockMvc.perform(get("/api/v1/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationId));
    }

    @Test
    void reserveCar_ValidReservationRequest_ReturnsCreatedStatus() throws Exception {
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setStartTime(LocalDateTime.now(Clock.systemUTC()).plusHours(26));
        reservationRequest.setDurationInMinutes(60);
        reservationRequest.setUserId(123);

        mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reservationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
