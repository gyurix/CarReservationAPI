package gyurix.carreservation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.db.repositories.ReservationRepository;
import gyurix.carreservation.dto.CarDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CarControllerIntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MockMvc mockMvc;

    private static String asJsonString(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    void addCar_ValidCar_ReturnsCreatedStatus() throws Exception {
        CarDTO carDTO = new CarDTO("C200", "Toyota", "Camry");

        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(carDTO)))
                .andExpect(status().isCreated());
    }

    private CarEntity createCar(String id, String make, String model) {
        CarEntity carEntity = new CarEntity();
        carEntity.setId(id);
        carEntity.setMake(make);
        carEntity.setModel(model);
        carRepository.save(carEntity);
        return carEntity;
    }

    @Test
    void deleteCar_ExistingCarId_ReturnsNoContentStatus() throws Exception {
        CarEntity car = createCar("C123", "Toyota", "Camry");
        carRepository.save(car);

        mockMvc.perform(delete("/api/v1/cars/{id}", car.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllCars_ReturnsAllCars() throws Exception {
        CarEntity car1 = createCar("C123", "Toyota", "Camry");
        CarEntity car2 = createCar("C456", "Honda", "Accord");
        carRepository.save(car1);
        carRepository.save(car2);

        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(car1.getId()))
                .andExpect(jsonPath("$[0].make").value(car1.getMake()))
                .andExpect(jsonPath("$[0].model").value(car1.getModel()))
                .andExpect(jsonPath("$[1].id").value(car2.getId()))
                .andExpect(jsonPath("$[1].make").value(car2.getMake()))
                .andExpect(jsonPath("$[1].model").value(car2.getModel()));
    }

    @Test
    void getCarById_ExistingCarId_ReturnsCar() throws Exception {
        CarEntity car = createCar("C123", "Toyota", "Camry");
        carRepository.save(car);

        mockMvc.perform(get("/api/v1/cars/{id}", car.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(car.getId()))
                .andExpect(jsonPath("$.make").value(car.getMake()))
                .andExpect(jsonPath("$.model").value(car.getModel()));
    }

    @AfterEach
    public void tearDown() {
        reservationRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    void updateCar_ExistingCarId_ReturnsNoContentStatus() throws Exception {
        CarEntity car = createCar("C123", "Toyota", "Camry");
        carRepository.save(car);
        CarDTO updatedCarDTO = new CarDTO("C123", "Honda", "Accord");

        mockMvc.perform(put("/api/v1/cars/{id}", car.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedCarDTO)))
                .andExpect(status().isNoContent());
    }
}
