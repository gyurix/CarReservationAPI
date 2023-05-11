package gyurix.carreservation.services;

import gyurix.carreservation.db.EntityMapper;
import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.dto.CarDTO;
import gyurix.carreservation.exceptions.car.CarAlreadyExistsException;
import gyurix.carreservation.exceptions.car.CarNotFoundException;
import gyurix.carreservation.exceptions.car.CarUnchangedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing cars.
 */
@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    private final EntityMapper entityMapper;

    /**
     * Adds a new car.
     *
     * @param car The car to add.
     * @throws CarAlreadyExistsException if a car with the same ID already exists.
     */
    public void addCar(CarDTO car) throws CarAlreadyExistsException {
        String id = car.getId();
        if (carRepository.existsById(id)) {
            throw new CarAlreadyExistsException(id);
        }
        carRepository.save(entityMapper.toCarEntity(car));
    }

    /**
     * Deletes a car by ID.
     *
     * @param id The ID of the car to delete.
     * @throws CarNotFoundException if the car with the specified ID does not exist.
     */
    public void deleteCar(String id) throws CarNotFoundException {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException(id);
        }
        carRepository.deleteById(id);
    }

    /**
     * Retrieves all cars.
     *
     * @return A list of all cars.
     */
    public List<CarDTO> getAllCars() {
        return carRepository.findAll(Sort.by(Sort.Order.asc("id"))).stream().map(entityMapper::toCarDTO).toList();
    }

    /**
     * Retrieves a car by ID.
     *
     * @param id The ID of the car to retrieve.
     * @return The car with the specified ID.
     * @throws CarNotFoundException if the car with the specified ID does not exist.
     */
    public CarDTO getCarById(String id) throws CarNotFoundException {
        return entityMapper.toCarDTO(carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id)));
    }

    /**
     * Updates an existing car.
     *
     * @param carDTO The updated car information.
     * @throws CarNotFoundException  if the car with the specified ID does not exist.
     * @throws CarUnchangedException if the updated car is the same as the existing car.
     */
    public void updateCar(CarDTO carDTO) throws CarNotFoundException, CarUnchangedException {
        String id = carDTO.getId();
        CarEntity oldEntity = carRepository.findById(id).orElseThrow(() -> new CarNotFoundException(id));
        CarEntity newEntity = entityMapper.toCarEntity(carDTO);
        if (oldEntity.equals(newEntity)) {
            throw new CarUnchangedException(id);
        }
        carRepository.save(newEntity);
    }
}
