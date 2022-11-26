package ru.practicum.shareit.item.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = new Item();
        ItemMapper.toItem(newItem, itemDto);
        User owner = userRepository.getUser(userId);
        if (owner == null) {
            throw new NotFoundValidationException(String.format("User with id=%d not found", userId));
        } else {
            newItem.setOwner(owner);
        }
        Item createdItem = itemRepository.createItem(newItem);
        log.info("Item created" + createdItem);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item updatedItem;
        itemValidator(itemRepository.getItem(itemId));
        Item item = new Item(itemRepository.getItem(itemId));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundValidationException("User is not owner of this item!");
        } else {
            ItemMapper.toItem(item, itemDto);
            updatedItem = itemRepository.updateItem(item);
            log.info("Item updated" + updatedItem);
            return ItemMapper.toItemDto(updatedItem);
        }
    }

    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemRepository.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItem(Long id) {
        itemValidator(itemRepository.getItem(id));
            return ItemMapper.toItemDto(itemRepository.getItem(id));
    }

    public List<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void removeItem(Long id) {
        itemValidator(itemRepository.getItem(id));
        itemRepository.removeItem(id);
    }

    private void itemValidator(Item item) {
        if (!itemRepository.getAllItems().contains(itemRepository.getItem(item.getId()))) {
            throw new NotFoundValidationException("Item with id " + itemRepository.getItem(item.getId()) + "not found");
        }
        if (item.getName().isBlank()) {
            throw new ConflictException("Name cant be blank");
        }
        if (item.getDescription().isBlank()) {
            throw new ConflictException("Description cant be blank");
        }
    }
}
