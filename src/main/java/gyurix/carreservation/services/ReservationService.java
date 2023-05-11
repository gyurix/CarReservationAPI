package gyurix.carreservation.services;

import gyurix.carreservation.db.EntityMapper;
import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.entities.ReservationEntity;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.db.repositories.ReservationRepository;
import gyurix.carreservation.dto.ReservationDTO;
import gyurix.carreservation.exceptions.reservation.NoCarAvailableException;
import gyurix.carreservation.exceptions.reservation.ReservationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing reservations.
 */
@Service
public class ReservationService {
    private final CarRepository carRepository;

    private final EntityMapper entityMapper;

    private final int maxDurationInMinutes;

    private final int minHoursAhead;

    private final ReservationRepository reservationRepository;

    /**
     * Constructs a new instance of {@link ReservationService} with the provided dependencies and configuration values.
     *
     * @param carRepository         The repository for car entities.
     * @param entityMapper          The mapper used for entity mapping.
     * @param reservationRepository The repository for reservation entities.
     * @param maxDurationInMinutes  The maximum duration allowed for a reservation in minutes.
     * @param minHoursAhead         The minimum number of hours required before making a reservation.
     */
    public ReservationService(CarRepository carRepository, EntityMapper entityMapper,
                              ReservationRepository reservationRepository,
                              @Value("${reservation.max-duration-in-minutes}") int maxDurationInMinutes,
                              @Value("${reservation.min-hours-ahead}") int minHoursAhead) {
        this.carRepository = carRepository;
        this.entityMapper = entityMapper;
        this.reservationRepository = reservationRepository;
        this.maxDurationInMinutes = maxDurationInMinutes;
        this.minHoursAhead = minHoursAhead;
    }

    /**
     * Retrieves all reservations.
     *
     * @return A list of all reservations.
     */
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll(Sort.by(Sort.Order.asc("startTime"),
                Sort.Order.asc("carId"))).stream().map(entityMapper::toReservationDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a reservation by ID.
     *
     * @param id The ID of the reservation to retrieve.
     * @return The reservation with the specified ID.
     * @throws ReservationNotFoundException if the reservation with the specified ID does not exist.
     */
    public ReservationDTO getReservationById(Integer id) throws ReservationNotFoundException {
        Optional<ReservationEntity> reservationEntity = reservationRepository.findById(id);
        if (reservationEntity.isPresent()) {
            return entityMapper.toReservationDTO(reservationEntity.get());
        } else {
            throw new ReservationNotFoundException(id);
        }
    }

    /**
     * Reserves a car for a specific time duration.
     *
     * @param startTime         The start time of the reservation.
     * @param durationInMinutes The duration of the reservation in minutes.
     * @param userId            The ID of the user making the reservation.
     * @return The reserved car and reservation details.
     * @throws ResponseStatusException if the reservation time or duration is invalid.
     * @throws NoCarAvailableException if no cars are available for the specified time duration.
     */
    public ReservationDTO reserveCar(LocalDateTime startTime, int durationInMinutes, int userId)
            throws ResponseStatusException, NoCarAvailableException {
        LocalDateTime currentTime = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime endTime = startTime.plusMinutes(durationInMinutes);

        if (startTime.isBefore(currentTime.plusHours(minHoursAhead))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation time must be at least 24 hours ahead.");
        }

        if (durationInMinutes > maxDurationInMinutes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum reservation duration is 2 hours.");
        }

        List<CarEntity> availableCars = carRepository.findAvailableCars(startTime, endTime);

        if (availableCars.isEmpty()) {
            throw new NoCarAvailableException();
        }

        CarEntity car = availableCars.get(0);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setCar(car);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setUserId(userId);

        reservationRepository.save(reservation);
        return entityMapper.toReservationDTO(reservation);
    }
}
