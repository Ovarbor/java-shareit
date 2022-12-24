package ru.practicum.shareit.item;
import org.mapstruct.Mapper;
<<<<<<< HEAD
import ru.practicum.shareit.item.dto.CreateItemRequest;
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

<<<<<<< HEAD
    Item toItem(CreateItemRequest createItemRequest);

=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    List<ItemDto> toDtoList(List<Item> items);
}