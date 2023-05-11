package gyurix.carreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for launching the Car Reservation application.
 */
@SpringBootApplication
public class CarReservationLauncher {
    /**
     * The main method that starts the Car Reservation application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CarReservationLauncher.class, args);
    }
}
