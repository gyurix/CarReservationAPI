package gyurix.carreservation.exceptions.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when attempting to update a car with no changes.
 */
public class CarUnchangedException extends ResponseStatusException {
    /**
     * Constructs a CarUnchangedException with the specified car ID.
     *
     * @param id the ID of the car that was not modified
     */
    public CarUnchangedException(String id) {
        super(HttpStatus.CONFLICT, String.format("Car %s was not modified.", id));
    }
}
