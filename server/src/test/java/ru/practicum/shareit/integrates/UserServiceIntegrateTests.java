package ru.practicum.shareit.integrates;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = {
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.datasource.url=jdbc:h2:mem:shareit;DB_CLOSE_ON_EXIT=FALSE",
                "spring.datasource.username=test",
                "spring.datasource.password=test"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrateTests {

    private final UserService userService;
    private final UserDto user1 = new UserDto(null, "user", "@ya.ru");
    private final UserDto user2 = new UserDto(null, "user1", "@ya1.ru");
    private final UserDto user3 = new UserDto(null, "user2", "@ya2.ru");

    @Test
    void testGetAllUsers() {
        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);
        List<UserDto> result = userService.getAllUsers();
        assertThat(result.size(), equalTo(3));
    }
}
