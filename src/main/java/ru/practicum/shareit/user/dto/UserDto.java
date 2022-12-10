package ru.practicum.shareit.user.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
<<<<<<< HEAD
import javax.validation.constraints.NotNull;
=======
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
<<<<<<< HEAD
    @NotBlank
    private String name;
    @Email
    @NotNull
=======
    private String name;
    @Email
    @NotBlank
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77
    private String email;
}
