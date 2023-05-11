package gyurix.carreservation.exceptions.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a reservation cannot be found.
 */
public class ReservationNotFoundException extends ResponseStatusException {
    /**
     * Constructs a ReservationNotFoundException with the specified reservation ID.
     *
     * @param id the ID of the reservation that was not found
     */
    public ReservationNotFoundException(Integer id) {
        super(HttpStatus.NOT_FOUND, String.format("Reservation %d was not found.", id));
    }
}
