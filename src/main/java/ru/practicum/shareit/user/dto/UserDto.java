package ru.practicum.shareit.user.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
<<<<<<< HEAD
import javax.validation.constraints.Null;
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotNull
    private String email;
}
