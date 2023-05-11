package gyurix.carreservation.services;

import gyurix.carreservation.db.EntityMapper;
import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.dto.CarDTO;
import gyurix.carreservation.exceptions.car.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    private CarService carService;

    @Mock
    private EntityMapper entityMapper;

    @BeforeEach
    void _setUp() {
        //noinspection resource
        MockitoAnnotations.openMocks(this);
        carService = new CarService(carRepository, entityMapper);
    }

    @Test
    void addCar_WithExistingCar_ShouldThrowCarAlreadyExistsException() {
        CarDTO carDTO = createCarDTO("C123");
        when(carRepository.existsById(carDTO.getId())).thenReturn(true);

        assertThrows(CarAlreadyExistsException.class, () -> carService.addCar(carDTO));
        verify(carRepository, never()).save(any());
    }

    @Test
    void addCar_WithNonExistingCar_ShouldSaveCar() {
        CarDTO carDTO = createCarDTO("C123");
        CarEntity carEntity = createCarEntity("C123");
        when(carRepository.existsById(carDTO.getId())).thenReturn(false);
        when(entityMapper.toCarEntity(carDTO)).thenReturn(carEntity);

        carService.addCar(carDTO);

        verify(carRepository).save(carEntity);
    }

    private CarDTO createCarDTO(String id) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(id);
        return carDTO;
    }

    private List<CarDTO> createCarDTOList(String... ids) {
        List<CarDTO> carDTOs = new ArrayList<>();
        for (String id : ids) {
            carDTOs.add(createCarDTO(id));
        }
        return carDTOs;
    }

    private CarEntity createCarEntity(String id) {
        CarEntity carEntity = new CarEntity();
        carEntity.setId(id);
        return carEntity;
    }

    private List<CarEntity> createCarEntityList(String... ids) {
        List<CarEntity> carEntities = new ArrayList<>();
        for (String id : ids) {
            carEntities.add(createCarEntity(id));
        }
        return carEntities;
    }

    @Test
    void deleteCar_WithExistingCar_ShouldDeleteCar() {
        String carId = "C123";
        when(carRepository.existsById(carId)).thenReturn(true);

        carService.deleteCar(carId);

        verify(carRepository).deleteById(carId);
    }

    @Test
    void deleteCar_WithNonExistingCar_ShouldThrowCarNotFoundException() {
        String carId = "C123";
        when(carRepository.existsById(carId)).thenReturn(false);

        assertThrows(CarNotFoundException.class, () -> carService.deleteCar(carId));
        verify(carRepository, never()).deleteById(any());
    }

    @Test
    void getAllCars_ShouldReturnAllCars() {
        List<CarEntity> carEntities = createCarEntityList("C123", "C456");
        when(carRepository.findAll((Sort) any())).thenReturn(carEntities);

        List<CarDTO> carDTOs = createCarDTOList("C123", "C456");
        when(entityMapper.toCarDTO(any())).thenReturn(carDTOs.get(0), carDTOs.get(1));

        List<CarDTO> result = carService.getAllCars();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("C123", result.get(0).getId());
        assertEquals("C456", result.get(1).getId());
    }

    @Test
    void getCarById_WithExistingCar_ShouldReturnCar() {
        String carId = "C123";
        CarEntity carEntity = createCarEntity(carId);
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));

        CarDTO carDTO = createCarDTO(carId);
        when(entityMapper.toCarDTO(carEntity)).thenReturn(carDTO);

        CarDTO result = carService.getCarById(carId);

        assertNotNull(result);
        assertEquals(carId, result.getId());
    }

    @Test
    void getCarById_WithNonExistingCar_ShouldThrowCarNotFoundException() {
        String carId = "C123";
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarById(carId));
    }

    @Test
    void updateCar_WithChangedCar_ShouldUpdateCar() {
        String carId = "C123";
        CarDTO carDTO = createCarDTO(carId);
        carDTO.setMake("Toyota");
        carDTO.setModel("Corolla");

        CarEntity oldEntity = createCarEntity(carId);
        CarEntity newEntity = createCarEntity(carId);
        newEntity.setMake("Toyota");
        newEntity.setModel("Corolla");

        when(carRepository.findById(carId)).thenReturn(Optional.of(oldEntity));
        when(entityMapper.toCarEntity(carDTO)).thenReturn(newEntity);
        when(carRepository.existsById(carId)).thenReturn(false);

        carService.updateCar(carDTO);

        verify(carRepository).save(newEntity);
    }

    @Test
    void updateCar_WithUnchangedCar_ShouldThrowCarUnchangedException() {
        String carId = "C123";
        CarDTO carDTO = createCarDTO(carId);

        CarEntity oldEntity = createCarEntity(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(oldEntity));
        when(entityMapper.toCarEntity(carDTO)).thenReturn(oldEntity);

        assertThrows(CarUnchangedException.class, () -> carService.updateCar(carDTO));
        verify(carRepository, never()).save(any());
    }
}