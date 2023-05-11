package gyurix.carreservation.exceptions.car;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a car cannot be found.
 */
public class CarNotFoundException extends ResponseStatusException {
    /**
     * Constructs a CarNotFoundException with the specified car ID.
     *
     * @param id the ID of the car that was not found
     */
    public CarNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND, String.format("Car %s was not found.", id));
    }
}
