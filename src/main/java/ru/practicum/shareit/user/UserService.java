package ru.practicum.shareit.user;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

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
    public UserDto updateUser(Long id, UserDto userDto) {
        Optional<User> oldUser = userRepository.findById(id);
        if (oldUser.isPresent()) {
            User updatedUser = userNameAndEmailUpdate(oldUser.get(), userDto);
            log.info("User with id: " + userDto.getId() + " updated");
            return userMapper.toUserDto(updatedUser);
        } else {
            throw new NotFoundValidationException("User with id: " + userDto.getId() + " not found.");
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            return userMapper.toUserDto(user.get());
        } else {
            throw new NotFoundValidationException("User with id: " + id + "not found");
        }
    }

    @Transactional
    public void removeUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundValidationException("User with id: " + id + "not found");
        }
    }

    private User userNameAndEmailUpdate(User oldUser, UserDto user) {
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