package ru.practicum.shareit.item.dto;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotNull
    private Long id;
    @NotBlank
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime creat;

}
