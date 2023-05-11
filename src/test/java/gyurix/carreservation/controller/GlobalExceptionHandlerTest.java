package gyurix.carreservation.controller;

import gyurix.carreservation.controllers.GlobalExceptionHandler;
import gyurix.carreservation.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleBindException_ValidException_ReturnsErrorResponse() {
        // Prepare test data
        BindException ex = mock(BindException.class);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String expectedStatus = status.toString();
        String expectedMessage = "Validation error";
        String expectedField = "fieldName";
        String expectedErrorMessage = "Field error message";

        // Mock the field error
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(expectedField);
        when(fieldError.getDefaultMessage()).thenReturn(expectedErrorMessage);

        // Mock the binding result
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Call the method under test
        ResponseEntity<Object> responseEntity = exceptionHandler.handleBindException(ex, null, status, null);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponseDTO);

        // Verify the error response
        ErrorResponseDTO errorResponse = (ErrorResponseDTO) responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(1, errorResponse.getErrors().size());

        // Verify the field error
        String fieldErrorMessage = errorResponse.getErrors().get(0);
        assertTrue(fieldErrorMessage.contains(expectedField));
        assertTrue(fieldErrorMessage.contains(expectedErrorMessage));
    }

    @Test
    void handleConstraintViolationException_MethodArgumentTypeMismatch_ReturnsErrorResponse() {
        // Prepare test data
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String expectedStatus = status.toString();
        String expectedMessage = "Validation error";
        String expectedArgumentName = "argumentName";
        String expectedErrorMessage = "Argument type mismatch";

        // Mock the exception
        when(ex.getName()).thenReturn(expectedArgumentName);
        when(ex.getMessage()).thenReturn(expectedErrorMessage);

        // Call the method under test
        ResponseEntity<ErrorResponseDTO> responseEntity = exceptionHandler.handleConstraintViolationException(ex);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        // Verify the error response
        ErrorResponseDTO errorResponse = responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(1, errorResponse.getErrors().size());

        // Verify the method argument type mismatch error
        String argumentErrorMessage = errorResponse.getErrors().get(0);
        assertTrue(argumentErrorMessage.contains(expectedArgumentName));
        assertTrue(argumentErrorMessage.contains(expectedErrorMessage));
    }

    @Test
    void handleConstraintViolationException_ValidException_ReturnsErrorResponse() {
        // Prepare test data
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String expectedStatus = status.toString();
        String expectedMessage = "Validation error";
        String expectedViolationMessage = "Violation error message";

        // Mock the constraint violation
        ConstraintViolation<?> constraintViolation = mock(ConstraintViolation.class);
        when(constraintViolation.getMessage()).thenReturn(expectedViolationMessage);

        // Mock the constraint violations set
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        constraintViolations.add(constraintViolation);

        // Mock the exception
        when(ex.getConstraintViolations()).thenReturn(constraintViolations);

        // Call the method under test
        ResponseEntity<ErrorResponseDTO> responseEntity = exceptionHandler.handleConstraintViolationException(ex);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        // Verify the error response
        ErrorResponseDTO errorResponse = responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(1, errorResponse.getErrors().size());

        // Verify the constraint violation
        String violationErrorMessage = errorResponse.getErrors().get(0);
        assertTrue(violationErrorMessage.contains(expectedViolationMessage));
    }

    @Test
    void handleGenericException_ValidException_ReturnsErrorResponse() {
        // Prepare test data
        Exception ex = new Exception("An unexpected error occurred");
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String expectedStatus = status.toString();
        String expectedMessage = "An unexpected error occurred";

        // Call the method under test
        ResponseEntity<ErrorResponseDTO> responseEntity = exceptionHandler.handleGenericException(ex);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        // Verify the error response
        ErrorResponseDTO errorResponse = responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(0, errorResponse.getErrors().size());
    }

    @Test
    void handleMethodArgumentNotValid_ValidException_ReturnsErrorResponse() {
        // Prepare test data
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String expectedStatus = status.toString();
        String expectedMessage = "Validation error";
        String expectedField = "fieldName";
        String expectedErrorMessage = "Field error message";

        // Mock the field error
        FieldError fieldError = mock(FieldError.class);
        when(fieldError.getField()).thenReturn(expectedField);
        when(fieldError.getDefaultMessage()).thenReturn(expectedErrorMessage);

        // Mock the binding result
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Call the method under test
        ResponseEntity<Object> responseEntity = exceptionHandler.handleMethodArgumentNotValid(ex, null, status, null);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof ErrorResponseDTO);

        // Verify the error response
        ErrorResponseDTO errorResponse = (ErrorResponseDTO) responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(1, errorResponse.getErrors().size());

        // Verify the field error
        String fieldErrorMessage = errorResponse.getErrors().get(0);
        assertTrue(fieldErrorMessage.contains(expectedField));
        assertTrue(fieldErrorMessage.contains(expectedErrorMessage));
    }


    @Test
    void handleResponseStatueException_ValidException_ReturnsErrorResponse() {
        // Prepare test data
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        HttpStatus status = ex.getStatus();
        String expectedStatus = status.toString();
        String expectedMessage = ex.getMessage();

        // Call the method under test
        ResponseEntity<ErrorResponseDTO> responseEntity = exceptionHandler.handleResponseStatueException(ex);

        // Verify the response entity
        assertEquals(status, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());

        // Verify the error response
        ErrorResponseDTO errorResponse = responseEntity.getBody();
        assertEquals(expectedStatus, errorResponse.getStatus());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(0, errorResponse.getErrors().size());
    }

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }
}

