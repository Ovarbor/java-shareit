package ru.practicum.shareit.item.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    @JsonProperty("available")
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;

//    public static class MyComparator implements Comparator<ItemDto> {
//        @Override
//        public int compare(ItemDto o1, ItemDto o2) {
//            return Long.compare(o1.getId(), o2.getId());
//        }
//    }
}
