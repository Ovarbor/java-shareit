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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private final User user1 = new User(1L, "user1", "@mail1.ru");
    private final User user2 = new User(2L, "user2", "@mail2.ru");

    private List<User> addUsersToList() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        return users;
    }

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
    void testUpdateUserNotFound() {
        UserDto newUser = userMapper.toUserDto(user2);
        UserDtoUpdate newUser1 = userMapper.toUserDtoUpdate(userMapper.toUser(newUser));
        Mockito.when(mockUserRepo.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        UserDtoUpdate savedUser = userMapper.toUserDtoUpdate(userMapper.toUser(service.createUser(newUser)));
        assertThat(newUser1, equalTo(savedUser));
        Mockito.when(mockUserRepo.findById(newUser.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.updateUser(newUser.getId(), savedUser));
    }

    @Test
    void testUpdateUser() {
        Mockito.when(mockUserRepo.findById(1L)).thenReturn(Optional.of(user1));
        UserDtoUpdate newUser = new UserDtoUpdate();
        newUser.setId(1L);
        newUser.setName("newName");
        newUser.setEmail(user1.getEmail());
        UserDtoUpdate resultUser = service.updateUser(newUser.getId(), newUser);
        assertThat(resultUser, equalTo(newUser));
        Mockito.when(mockUserRepo.findById(2L)).thenReturn(Optional.of(user2));
        UserDtoUpdate newUser2 = new UserDtoUpdate();
        newUser2.setId(2L);
        newUser2.setName("notTestUser");
        newUser2.setEmail("test49@ya.ru");
        UserDtoUpdate resultUser2 = service.updateUser(newUser2.getId(), newUser2);
        assertThat(resultUser2, equalTo(newUser2));
    }

    @Test
    void testGetAllUsers() {
        List<UserDto> expected = userMapper.toDtoList(addUsersToList());
        Mockito.when(mockUserRepo.findAll()).thenReturn(addUsersToList());
        List<UserDto> result = service.getAllUsers();
        assertThat(expected, equalTo(result));
    }

    @Test
    void deleteUser() {
        service.removeUser(user1.getId());
        verify(mockUserRepo, Mockito.times(1)).deleteById(user1.getId());
    }

    @Test
    void getUser() {
        when(mockUserRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        assertEquals(userMapper.toUserDto(user1), service.getUser(user1.getId()));
    }

    @Test
    void testGetUserNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getUser(1L));
    }
}
