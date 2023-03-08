package ru.practicum.shareit.user;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(201).body(userService.createUser(userDto));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDtoUpdate> updateUser(@PathVariable Long userId,
                                                    @RequestBody UserDtoUpdate userDto) {
        return ResponseEntity.ok().body(userService.updateUser(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getUser(userId));
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }
}
