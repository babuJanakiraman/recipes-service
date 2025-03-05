package nl.abn.assessment.recipesservice.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import nl.abn.assessment.recipesservice.exception.RecipeNotFoundException;
import nl.abn.assessment.recipesservice.model.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class RecipeServiceExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred on the server.";
    private static final String INVALID_INPUT_MESSAGE = "Invalid input";
    private static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe Not Found";


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid: {}", ex.getMessage());

        StringBuilder detailedMessage = new StringBuilder("Validation failed for: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                detailedMessage.append(String.format("Field '%s' %s. ", error.getField(), error.getDefaultMessage()))
        );

        return buildErrorResponse(BAD_REQUEST.value(), INVALID_INPUT_MESSAGE, detailedMessage.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Constraint violation: {}", ex.getMessage());
        return buildErrorResponse(BAD_REQUEST.value(), INVALID_INPUT_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("Method argument type mismatch: {}", ex.getMessage());
        return buildErrorResponse(BAD_REQUEST.value(), INVALID_INPUT_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HTTP message not readable: {}", ex.getMessage());
        return buildErrorResponse(BAD_REQUEST.value(), INVALID_INPUT_MESSAGE, ex.getLocalizedMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("HTTP Method Not Supported: {}", ex.getMessage());
        return buildErrorResponse(METHOD_NOT_ALLOWED.value(), INVALID_INPUT_MESSAGE, ex.getLocalizedMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return buildErrorResponse(BAD_REQUEST.value(), INVALID_INPUT_MESSAGE, ex.getLocalizedMessage());
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecipeNotFoundException(RecipeNotFoundException ex) {
        log.error("Recipe Not Found: {}", ex.getMessage());
        return buildErrorResponse(NOT_FOUND.value(), RECIPE_NOT_FOUND_MESSAGE, ex.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return buildErrorResponse(500, INTERNAL_SERVER_ERROR_MESSAGE, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(int status, String message, String detailedMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(status);
        errorResponse.setMessage(message);
        errorResponse.setDetailedMessage(detailedMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }


}
