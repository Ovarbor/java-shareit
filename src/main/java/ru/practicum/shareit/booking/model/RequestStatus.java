package ru.practicum.shareit.booking.model;
import ru.practicum.shareit.exceptions.IllegalRequestException;

public enum RequestStatus {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
}
