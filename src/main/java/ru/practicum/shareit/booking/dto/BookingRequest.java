package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookingRequest {
    @Positive
    private Long itemId;
<<<<<<< HEAD
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
=======
    @FutureOrPresent @NotNull
    private LocalDateTime start;
    @Future @NotNull
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    private LocalDateTime end;
}
