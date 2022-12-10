package ru.practicum.shareit.user;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto userCreated = userService.createUser(userDto);
        return ResponseEntity.status(201).body(userCreated);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto userUpdated = userService.updateUser(userId, userDto);
        return ResponseEntity.ok().body(userUpdated);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        UserDto user = userService.getUser(userId);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok().body(users);
    }
}
