package gyurix.carreservation.services;

import gyurix.carreservation.db.EntityMapper;
import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.entities.ReservationEntity;
import gyurix.carreservation.db.repositories.CarRepository;
import gyurix.carreservation.db.repositories.ReservationRepository;
import gyurix.carreservation.dto.ReservationDTO;
import gyurix.carreservation.exceptions.reservation.NoCarAvailableException;
import gyurix.carreservation.exceptions.reservation.ReservationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private ReservationRepository reservationRepository;

    private ReservationService reservationService;

    @BeforeEach
    void _setUp() {
        //noinspection resource
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(carRepository, entityMapper, reservationRepository,
                120, 24);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        List<ReservationEntity> reservationEntities = new ArrayList<>();
        ReservationEntity reservationEntity1 = new ReservationEntity();
        ReservationEntity reservationEntity2 = new ReservationEntity();
        reservationEntities.add(reservationEntity1);
        reservationEntities.add(reservationEntity2);

        when(reservationRepository.findAll((Sort) any())).thenReturn(reservationEntities);

        ReservationDTO reservationDTO1 = new ReservationDTO();
        ReservationDTO reservationDTO2 = new ReservationDTO();

        when(entityMapper.toReservationDTO(any())).thenReturn(reservationDTO1, reservationDTO2);

        List<ReservationDTO> result = reservationService.getAllReservations();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(reservationDTO1, result.get(0));
        assertSame(reservationDTO2, result.get(1));
    }

    @Test
    void getReservationById_WithExistingReservation_ShouldReturnReservation() {
        int reservationId = 123;
        ReservationEntity reservationEntity = new ReservationEntity();
        ReservationDTO expectedReservationDTO = new ReservationDTO();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(entityMapper.toReservationDTO(reservationEntity)).thenReturn(expectedReservationDTO);

        ReservationDTO result = reservationService.getReservationById(reservationId);

        assertNotNull(result);
        assertSame(expectedReservationDTO, result);
    }

    @Test
    void getReservationById_WithNonExistingReservation_ShouldThrowReservationNotFoundException() {
        int reservationId = 123;

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById(reservationId));
    }

    @Test
    void reserveCar_WithExceedingMaxDuration_ShouldThrowResponseStatusException() {
        LocalDateTime startTime = LocalDateTime.now(Clock.systemUTC()).plusHours(25);
        int durationInMinutes = 130;
        int userId = 123;

        assertThrows(ResponseStatusException.class, () ->
                reservationService.reserveCar(startTime, durationInMinutes, userId));

        verify(carRepository, never()).findAvailableCars(any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveCar_WithNoAvailableCars_ShouldThrowNoCarAvailableException() {
        LocalDateTime startTime = LocalDateTime.now(Clock.systemUTC()).plusHours(25);
        int durationInMinutes = 90;
        int userId = 123;

        when(carRepository.findAvailableCars(any(), any())).thenReturn(List.of());

        assertThrows(NoCarAvailableException.class, () ->
                reservationService.reserveCar(startTime, durationInMinutes, userId));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveCar_WithStartTimeBeforeMinimumReservationTime_ShouldThrowResponseStatusException() {
        LocalDateTime startTime = LocalDateTime.now(Clock.systemUTC()).plusHours(23);
        int durationInMinutes = 90;
        int userId = 123;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                reservationService.reserveCar(startTime, durationInMinutes, userId));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(carRepository, never()).findAvailableCars(any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserveCar_WithValidInput_ShouldReserveCarAndReturnReservationDTO() {
        LocalDateTime startTime = LocalDateTime.now(Clock.systemUTC()).plusHours(25);
        int durationInMinutes = 90;
        int userId = 123;

        LocalDateTime endTime = startTime.plusMinutes(durationInMinutes);

        CarEntity availableCar = new CarEntity();
        when(carRepository.findAvailableCars(startTime, endTime)).thenReturn(List.of(availableCar));

        ReservationEntity reservationEntity = new ReservationEntity();
        ReservationDTO expectedReservationDTO = new ReservationDTO();

        when(entityMapper.toReservationDTO(any())).thenReturn(expectedReservationDTO);
        when(reservationRepository.save(any())).thenReturn(reservationEntity);

        ReservationDTO result = reservationService.reserveCar(startTime, durationInMinutes, userId);

        assertNotNull(result);
        assertSame(expectedReservationDTO, result);
        verify(reservationRepository).save(any());
    }
}
