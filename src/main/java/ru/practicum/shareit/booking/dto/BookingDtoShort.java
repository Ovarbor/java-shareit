package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Lombok;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShort {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Long bookerId;
}
