package ru.practicum.shareit.user;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.user.dto.UserDto;
<<<<<<< HEAD
import ru.practicum.shareit.user.dto.UserDtoUpdate;
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import ru.practicum.shareit.user.model.User;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("User with id: " + userDto.getId() + " created");
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Transactional
<<<<<<< HEAD
    public UserDtoUpdate updateUser(Long id, UserDtoUpdate userDto) {
=======
    public UserDto updateUser(Long id, UserDto userDto) {
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
        User oldUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userDto.getId() + " not found."));
        User updatedUser = userNameAndEmailUpdate(oldUser, userDto);
        log.info("User with id: " + userDto.getId() + " updated");
<<<<<<< HEAD
        return userMapper.toUserDtoUpdate(updatedUser);
=======
        return userMapper.toUserDto(updatedUser);
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + id + "not found"));
        return userMapper.toUserDto(user);
    }

    @Transactional
    public void removeUser(Long id) {
        userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundValidationException("User with id: " + id + "not found"));
        userRepository.deleteById(id);
    }

<<<<<<< HEAD
    private User userNameAndEmailUpdate(User oldUser, UserDtoUpdate user) {
=======
    private User userNameAndEmailUpdate(User oldUser, UserDto user) {
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
        if (user.getEmail() != null) {
            if (!user.getEmail().isBlank()) {
                oldUser.setEmail(user.getEmail());
            }
        }
        if (user.getName() != null) {
            if (!user.getName().isBlank()) {
                oldUser.setName(user.getName());
            }
        }
        return oldUser;
    }
}
