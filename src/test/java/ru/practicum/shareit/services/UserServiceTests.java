package ru.practicum.shareit.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserMapperImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    List<User> userList = new ArrayList<>();
    private final User user1 = new User(1L, "user1", "@mail1.ru");
    private final User user2 = new User(2L, "user2", "@mail2.ru");

    @Mock
    UserRepository mockUserRepo;
    UserMapper userMapper = new UserMapperImpl();
    UserService service;

    @BeforeEach
    void makeService() {
        service = new UserService(mockUserRepo, userMapper);
    }

    @Test
    void testUserMapper() {
        UserDto userDto = userMapper.toUserDto(user1);
        assertThat(user1.getId(), equalTo(userDto.getId()));
        assertThat(user1.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user1.getName(), equalTo(userDto.getName()));
        User userBack = userMapper.toUser(userDto);
        assertThat(user1.getId(), equalTo(userBack.getId()));
        assertThat(user1.getEmail(), equalTo(userBack.getEmail()));
        assertThat(user1.getName(), equalTo(userBack.getName()));
        assertEquals(user1, userBack);
    }

    @Test
    void testUserCreate() {
        UserDto newUser = userMapper.toUserDto(user1);
        Mockito.when(mockUserRepo.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        UserDto savedUser = service.createUser(newUser);
        assertThat(newUser, equalTo(savedUser));
    }

    @Test
    void testPatchUserNotFound() {
        UserDto newUser = userMapper.toUserDto(user2);
        UserDtoUpdate newUser1 = userMapper.toUserDtoUpdate(userMapper.toUser(newUser));
        Mockito.when(mockUserRepo.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        UserDtoUpdate savedUser = userMapper.toUserDtoUpdate(userMapper.toUser(service.createUser(newUser)));
        assertThat(newUser1, equalTo(savedUser));
        Mockito.when(mockUserRepo.findById(newUser.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.updateUser(newUser.getId(), savedUser));
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> expected = userMapper.toDtoList(userList);
        Mockito.when(mockUserRepo.findAll()).thenReturn(userList);
        List<UserDto> result = service.getAllUsers();
        assertThat(expected, equalTo(result));
    }

    @Test
    void testGetUser() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(user2));
        assertThat(userMapper.toUserDto(user2), equalTo(service.getUser(user2.getId())));
        System.out.println(service.getUser(user2.getId()));
    }

    @Test
    void testGetUserNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getUser(1L));
    }
}
