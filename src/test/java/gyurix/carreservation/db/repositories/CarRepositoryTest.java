package gyurix.carreservation.db.repositories;

import gyurix.carreservation.db.entities.CarEntity;
import gyurix.carreservation.db.entities.ReservationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private CarEntity createCar(String id) {
        CarEntity car = new CarEntity();
        car.setId(id);
        return carRepository.save(car);
    }

    private void createReservation(CarEntity car, LocalDateTime startTime, LocalDateTime endTime) {
        ReservationEntity reservation = new ReservationEntity();
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setCar(car);
        reservationRepository.save(reservation);
    }

    @BeforeEach
    void setUp() {
        CarEntity car1 = createCar("C123");
        CarEntity car2 = createCar("C456");
        CarEntity car3 = createCar("C789");

        createReservation(car1, toUtcTime(10, 10), toUtcTime(10, 50));
        createReservation(car1, toUtcTime(11, 20), toUtcTime(12, 40));
        createReservation(car2, toUtcTime(12, 30), toUtcTime(14, 30));
        createReservation(car3, toUtcTime(13, 40), toUtcTime(15, 20));
    }

    @Test
    public void testAllCarsAvailableAfterEnd() {
        assertEquals(3, carRepository.findAvailableCars(toUtcTime(16, 0), toUtcTime(17, 0)).size());
        assertEquals(3, carRepository.findAvailableCars(toUtcTime(15, 20), toUtcTime(16, 0)).size());
    }

    @Test
    public void testAllCarsAvailableAtTimeGap() {
        assertEquals(3, carRepository.findAvailableCars(toUtcTime(10, 50), toUtcTime(11, 20)).size());
    }

    @Test
    public void testAllCarsAvailableBeforeStart() {
        assertEquals(3, carRepository.findAvailableCars(toUtcTime(9, 0), toUtcTime(10, 0)).size());
        assertEquals(3, carRepository.findAvailableCars(toUtcTime(9, 0), toUtcTime(10, 10)).size());
    }

    @Test
    public void testCarUnavailableAfterStart() {
        assertEquals(2, carRepository.findAvailableCars(toUtcTime(9, 0), toUtcTime(10, 11)).size());
        assertEquals(1, carRepository.findAvailableCars(toUtcTime(12, 35), toUtcTime(13, 10)).size());
        assertEquals(0, carRepository.findAvailableCars(toUtcTime(10, 10), toUtcTime(13, 41)).size());
    }

    @Test
    public void testCarUnavailableBeforeEnd() {
        assertEquals(2, carRepository.findAvailableCars(toUtcTime(10, 49), toUtcTime(12, 30)).size());
        assertEquals(1, carRepository.findAvailableCars(toUtcTime(10, 49), toUtcTime(12, 31)).size());
        assertEquals(0, carRepository.findAvailableCars(toUtcTime(10, 49), toUtcTime(13, 41)).size());
    }

    private LocalDateTime toUtcTime(int hour, int minute) {
        return LocalDateTime.of(2023, 5, 11, hour, minute)
                .atOffset(ZoneOffset.UTC)
                .toLocalDateTime();
    }

}

