package ru.practicum.shareit.user;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
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
    public UserDtoUpdate updateUser(Long id, UserDtoUpdate userDto) {
        User oldUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundValidationException("User with id: " + userDto.getId() + " not found."));
        User updatedUser = userNameAndEmailUpdate(oldUser, userDto);
        log.info("User with id: " + userDto.getId() + " updated");
        return userMapper.toUserDtoUpdate(updatedUser);
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

    private User userNameAndEmailUpdate(User oldUser, UserDtoUpdate user) {
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
