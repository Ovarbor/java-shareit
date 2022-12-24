package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingDtoItem item;
    private BookingDtoBooker booker;
    private Status status;
<<<<<<< HEAD

    public Long getItemId() {
        return item.getId();
    }

    public Long getBookerId() {
        return booker.getId();
    }
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
}
