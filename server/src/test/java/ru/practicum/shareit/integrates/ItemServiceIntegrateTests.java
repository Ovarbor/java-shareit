package ru.practicum.shareit.integrates;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
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
public class ItemServiceIntegrateTests {

    private final ItemService itemService;
    private final UserService userService;
    private final CreateItemRequest itemRequest1 =
            new CreateItemRequest(1L, "item1", "itemdesc1", Boolean.TRUE, null);
    private final CreateItemRequest itemRequest2 =
            new CreateItemRequest(2L, "item2", "itemdesc2", Boolean.TRUE, null);
    private final CreateItemRequest itemRequest3 =
            new CreateItemRequest(3L, "item3", "itemdesc3", Boolean.TRUE, null);
    private final UserDto userDto1 = new UserDto(1L, "user1", "user1@email");
    private final UserDto userDto2 = new UserDto(2L, "user2", "user2@email");

    @Test
    void testGetUserItems() {
        UserDto owner1 = userService.createUser(userDto1);
        UserDto owner2 = userService.createUser(userDto2);

        ItemDto item1 = itemService.createItem(itemRequest1, owner1.getId());
        ItemDto item2 = itemService.createItem(itemRequest2, owner1.getId());
        ItemDto item3 = itemService.createItem(itemRequest3, owner2.getId());

        List<ItemDto> result = itemService.getAllByOwnerId(owner1.getId(), 0, 20);
        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getName(), equalTo(item1.getName()));
        assertThat(result.get(1).getName(), equalTo(item2.getName()));

        List<ItemDto> result2 = itemService.getAllByOwnerId(owner2.getId(), 0, 20);
        assertThat(result2.size(), equalTo(1));
        assertThat(result2.get(0).getName(), equalTo(item3.getName()));
    }
}
