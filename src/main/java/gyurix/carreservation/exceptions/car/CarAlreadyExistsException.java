package gyurix.carreservation.exceptions.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when trying to add a car that already exists.
 */
public class CarAlreadyExistsException extends ResponseStatusException {
    /**
     * Constructs a CarAlreadyExistsException with the specified car ID.
     *
     * @param id the ID of the car that already exists
     */
    public CarAlreadyExistsException(String id) {
        super(HttpStatus.CONFLICT, String.format("Car %s exists already.", id));
    }
}
