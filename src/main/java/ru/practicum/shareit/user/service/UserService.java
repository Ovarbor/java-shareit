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
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        emailValidator(user);
        User newUser = userRepository.createUser(user);
        log.info("User created" + newUser);
        return userMapper.toUserDto(newUser);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        User oldUser = userRepository.getUser(id);
        userIdValidator(oldUser);
        User user = userMapper.toUser(userDto);
        userNameAndEmailValidator(oldUser, user);
        User changedUser = userRepository.updateUser(oldUser);
        log.info("user with id = {} is changed {}.", changedUser.getId(), changedUser);
        return userMapper.toUserDto(changedUser);
    }


    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        userIdValidator(userRepository.getUser(id));
        return userMapper.toUserDto(userRepository.getUser(id));
    }

    public void removeUser(Long id) {
        userIdValidator(userRepository.getUser(id));
        userRepository.removeUser(id);
    }

    private void userIdValidator(User user) {
        if (!userRepository.getAllUsers().contains(userRepository.getUser(user.getId()))) {
            throw new NotFoundValidationException("User with id " + userRepository.getUser(user.getId()) + "not found");
        }
        if (user.getName().isBlank()) {
            throw new ConflictException("Name cant be blank");
        }
        if (user.getEmail().isBlank()) {
            throw new ConflictException("Description cant be blank");
        }
    }

    private void userNameAndEmailValidator(User oldUser, User user) {
        if (user.getEmail() != null) {
            emailValidator(user);
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
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
