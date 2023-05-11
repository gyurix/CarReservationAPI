package gyurix.carreservation.exceptions.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when no car is available for a requested reservation time.
 */
public class NoCarAvailableException extends ResponseStatusException {
    /**
     * Constructs a NoCarAvailableException.
     */
    public NoCarAvailableException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "No car available for the requested time.");
    }
}
