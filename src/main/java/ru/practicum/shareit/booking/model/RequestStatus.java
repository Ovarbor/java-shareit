package ru.practicum.shareit.booking.model;
import ru.practicum.shareit.exceptions.IllegalRequestException;

public enum RequestStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static RequestStatus parseState(String line) {
        RequestStatus state;
        try {
            state = RequestStatus.valueOf(line);
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestException("Unknown state: " + line);
        }
        return state;
    }
}
