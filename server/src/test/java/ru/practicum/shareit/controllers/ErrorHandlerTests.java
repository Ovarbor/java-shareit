package ru.practicum.shareit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ExceptionsHandler;
import ru.practicum.shareit.exceptions.IllegalRequestException;
import ru.practicum.shareit.exceptions.NotFoundValidationException;

import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorHandlerTests {

    private ExceptionsHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new ExceptionsHandler();
    }

    @Test
    void testNotFoundValidationException() {
        ResponseEntity<ExceptionsHandler.ErrorMessage> actualHandleNotFoundExceptionResult = exceptionHandler
                .notFoundValidationException(new NotFoundValidationException("404"));
        assertEquals("404", Objects.requireNonNull(actualHandleNotFoundExceptionResult.getBody()).getMessage());
        assertEquals(HttpStatus.NOT_FOUND, actualHandleNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleNotFoundExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testIllegalRequestException() {
        ResponseEntity<ExceptionsHandler.ErrorMessage> actualHandleNotFoundExceptionResult = exceptionHandler
                .illegalRequestException(new IllegalRequestException("400"));
        assertEquals("400", Objects.requireNonNull(actualHandleNotFoundExceptionResult.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, actualHandleNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleNotFoundExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testConstraintViolationException() {
        ResponseEntity<ExceptionsHandler.ErrorMessage> actualHandleNotFoundExceptionResult = exceptionHandler
                .onConstraintValidationException(new ConstraintViolationException("400", Set.of()));
        assertEquals("400", Objects.requireNonNull(actualHandleNotFoundExceptionResult.getBody()).getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, actualHandleNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleNotFoundExceptionResult.getHeaders().isEmpty());
    }

    @Test
    void testConflictException() {
        ResponseEntity<ExceptionsHandler.ErrorMessage> actualHandleNotFoundExceptionResult = exceptionHandler
                .conflictException(new ConflictException("409"));
        assertEquals("409", Objects.requireNonNull(actualHandleNotFoundExceptionResult.getBody()).getMessage());
        assertEquals(HttpStatus.CONFLICT, actualHandleNotFoundExceptionResult.getStatusCode());
        assertTrue(actualHandleNotFoundExceptionResult.getHeaders().isEmpty());
    }
}
