package gyurix.carreservation.controllers;

import gyurix.carreservation.dto.ErrorResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Global exception handler that handles and provides consistent error responses for various exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Creates an {@link ErrorResponseDTO} with the validation errors from the {@link BindException}.
     *
     * @param ex     The {@link BindException} instance.
     * @param status The HTTP status of the response.
     * @return An {@link ErrorResponseDTO} with the validation errors.
     */
    private ResponseEntity<Object> getErrors(BindException ex, HttpStatus status) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status.toString(), "Validation error", errors);
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handles the {@link BindException} and creates a response entity with the validation errors.
     *
     * @param ex      The {@link BindException} instance.
     * @param headers The HTTP headers of the response.
     * @param status  The HTTP status of the response.
     * @param request The web request.
     * @return A response entity with the validation errors.
     */
    @Override
    public ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
                                                      WebRequest request) {
        return getErrors(ex, status);
    }

    /**
     * Handles the {@link ConstraintViolationException} and {@link MethodArgumentTypeMismatchException}
     * and creates a response entity with the validation errors.
     *
     * @param ex The exception instance.
     * @return A response entity with the validation errors.
     */
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(Exception ex) {
        List<String> errors = new ArrayList<>();
        if (ex instanceof ConstraintViolationException cve) {
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
            }
        } else if (ex instanceof MethodArgumentTypeMismatchException mismatchException) {
            errors.add(mismatchException.getName() + ": " + mismatchException.getMessage());
        }
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(BAD_REQUEST.toString(), "Validation error", errors);
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles generic exceptions and creates a response entity with an appropriate error message.
     *
     * @param ex The exception instance.
     * @return A response entity with the error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(INTERNAL_SERVER_ERROR.toString(),
                "An unexpected error occurred", List.of());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles the {@link MethodArgumentNotValidException} and creates a response entity with the validation errors.
     *
     * @param ex      The {@link MethodArgumentNotValidException} instance.
     * @param headers The HTTP headers of the response.
     * @param status  The HTTP status of the response.
     * @param request The web request.
     * @return A response entity with the validation errors.
     */
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        return getErrors(ex, status);
    }

    /**
     * Handles the {@link ResponseStatusException} and creates a response entity with the provided status and message.
     *
     * @param ex The {@link ResponseStatusException} instance.
     * @return A response entity with the provided status and message.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatueException(ResponseStatusException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getStatus().toString(), ex.getReason(), List.of());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
}