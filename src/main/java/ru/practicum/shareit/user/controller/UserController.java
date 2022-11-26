package ru.practicum.shareit.user.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }


    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }


    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }
}
