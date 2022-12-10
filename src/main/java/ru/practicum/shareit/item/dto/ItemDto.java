package ru.practicum.shareit.item.dto;
<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
=======
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
<<<<<<< HEAD

=======
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
<<<<<<< HEAD
    @JsonProperty("available")
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
=======
    private Boolean available;
    private User owner;
    private ItemRequest request;
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77
}
