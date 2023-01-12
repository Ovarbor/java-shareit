package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.DatesAreNotCorrectException;
import ru.practicum.shareit.exceptions.IncorrectStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createNewBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                                   @Validated @RequestBody BookingDto bookingDto) {
        checkDates(bookingDto);
        return bookingClient.createNewBooking(bookingDto, id);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(id, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            return bookingClient.getAllBookingsByUserId(id, bookingState, from, size);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(id, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfCurrentUserItems(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            return bookingClient.getAllBookingsOfCurrentUserItems(id, bookingState, from, size);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }
    }

    private void checkDates(BookingDto booking) {
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new DatesAreNotCorrectException("Дата начала после даты окончания");
        }
    }
}
