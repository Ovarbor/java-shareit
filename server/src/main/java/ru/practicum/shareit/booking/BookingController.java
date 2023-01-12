package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.RequestStatus;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody @Valid BookingRequest request,
                             @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return ResponseEntity.status(201).body(bookingService.create(request, requesterId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> setStatusByOwner(@PathVariable Long bookingId,
                                       @RequestParam("approved") Boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok().body(bookingService.changeStatusByOwner(bookingId, approved, ownerId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll(@RequestParam(name = "state", required = false, defaultValue = "ALL") RequestStatus state,
                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return ResponseEntity.ok().body(bookingService.getAllByUser(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") RequestStatus state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return ResponseEntity.ok().body(bookingService.getAllByUserOwner(state, userId, from, size));
    }
}
