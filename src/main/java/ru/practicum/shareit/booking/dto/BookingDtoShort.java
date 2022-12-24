package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
<<<<<<< HEAD
import lombok.Lombok;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

=======
import lombok.NoArgsConstructor;
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoShort {
<<<<<<< HEAD
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Long bookerId;
=======
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private Integer bookerId;
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
}
