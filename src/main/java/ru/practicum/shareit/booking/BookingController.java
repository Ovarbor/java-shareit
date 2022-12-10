package ru.practicum.shareit.booking;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody @Valid BookingRequest request,
                             @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        BookingDto bookingCreated = bookingService.create(request, requesterId);
        return ResponseEntity.status(201).body(bookingCreated);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> setStatusByOwner(@PathVariable Long bookingId,
                                       @RequestParam("approved") Boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        BookingDto updatedBooking = bookingService.changeStatusByOwner(bookingId, approved, ownerId);
        return ResponseEntity.ok().body(updatedBooking);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        BookingDto booking = bookingService.getById(bookingId, userId);
        return ResponseEntity.ok().body(booking);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAll(@RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        List<BookingDto> bookings = bookingService.getAllByUser(state, userId, from, size);
        return ResponseEntity.ok().body(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        List<BookingDto> ownerBookings = bookingService.getAllByUserOwner(state, userId, from, size);
        return ResponseEntity.ok().body(ownerBookings);
    }
}
