package ru.practicum.shareit.item;
import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item toItem(ItemDto itemDto);

    Item toItem(CreateItemRequest createItemRequest);

    List<ItemDto> toDtoList(List<Item> items);
}