package ru.practicum.shareit.exceptions;

public class NotFoundValidationException extends RuntimeException {
    public NotFoundValidationException(String message) {
        super(message);
    }
}
