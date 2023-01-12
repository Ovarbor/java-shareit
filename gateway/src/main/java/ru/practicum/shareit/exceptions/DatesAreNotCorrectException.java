package ru.practicum.shareit.exceptions;

public class DatesAreNotCorrectException extends RuntimeException {
    public DatesAreNotCorrectException(String message) {
        super(message);
    }
}
