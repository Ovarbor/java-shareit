package ru.practicum.shareit.user.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = new User();
        UserMapper.toUser(user, userDto);
        emailValidator(user);
        User newUser = userRepository.createUser(user);
        log.info("User created" + newUser);
        return UserMapper.toUserDto(newUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User updatedUser;
        userValidator(userRepository.getUser(id));
        User user = new User(userRepository.getUser(id));
        UserMapper.toUser(user, userDto);
        user.setId(id);
        emailValidator(user);
        updatedUser = userRepository.updateUser(user);
        log.info("User updated" + updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        userValidator(userRepository.getUser(id));
        return UserMapper.toUserDto(userRepository.getUser(id));
    }

    public void removeUser(Long id) {
        userValidator(userRepository.getUser(id));
        userRepository.removeUser(id);
    }

    protected void userValidator(User user) {
        if (!userRepository.getAllUsers().contains(userRepository.getUser(user.getId()))) {
            throw new NotFoundValidationException("User with id " + userRepository.getUser(user.getId()) + "not found");
        }
    }

    private void emailValidator(User user) {
            if (emailCheck(user)) {
                throw new ConflictException("This email is already in use");
            }
    }

    private Boolean emailCheck(User user) {
       return userRepository.getAllUsers()
                .stream()
                .anyMatch(u -> u.getEmail().contains(user.getEmail()) && !Objects.equals(u.getId(), user.getId()));
    }
}
