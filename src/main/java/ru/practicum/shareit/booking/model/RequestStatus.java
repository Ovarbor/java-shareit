package ru.practicum.shareit.booking.model;
import ru.practicum.shareit.exceptions.IllegalRequestException;

public enum RequestStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
<<<<<<< HEAD
=======

    public static RequestStatus parseState(String line) {
        RequestStatus state;
        try {
            state = RequestStatus.valueOf(line);
        } catch (IllegalArgumentException e) {
            throw new IllegalRequestException("Unknown state: " + line);
        }
        return state;
    }
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
}
