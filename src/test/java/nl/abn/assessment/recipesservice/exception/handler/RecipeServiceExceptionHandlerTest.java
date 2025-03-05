package nl.abn.assessment.recipesservice.exception.handler;

import nl.abn.assessment.recipesservice.exception.RecipeNotFoundException;
import nl.abn.assessment.recipesservice.model.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecipeServiceExceptionHandlerTest {

    private final RecipeServiceExceptionHandler handler = new RecipeServiceExceptionHandler();

    @Test
    void handleMethodArgumentNotValidException_returnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleConstraintViolationException_returnsBadRequest() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatchException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleHttpMessageNotReadableException_returnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleHttpRequestMethodNotSupportedException_returnsMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex = mock(HttpRequestMethodNotSupportedException.class);
        ResponseEntity<ErrorResponse> response = handler.handleHttpRequestMethodNotSupportedException(ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
    }

    @Test
    void handleBadRequestException_returnsBadRequest() {
        BadRequestException ex = mock(BadRequestException.class);
        ResponseEntity<ErrorResponse> response = handler.handleBadRequestException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleRecipeNotFoundException_returnsNotFound() {
        RecipeNotFoundException ex = mock(RecipeNotFoundException.class);
        ResponseEntity<ErrorResponse> response = handler.handleRecipeNotFoundException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleException_returnsInternalServerError() {
        Exception ex = mock(Exception.class);
        ResponseEntity<ErrorResponse> response = handler.handleException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}