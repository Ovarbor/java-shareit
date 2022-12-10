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
    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = itemMapper.toItem(itemDto);
        User owner = userRepository.getUser(userId);
        itemOwnerCheckValidator(owner, newItem, userId);
        Item createdItem = itemRepository.createItem(newItem);
        log.info("Item created" + createdItem);
        return itemMapper.toItemDto(createdItem);
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item  = itemMapper.toItem(itemDto);
        userIdValidator(userId);
        Item oldItem = itemRepository.getItem(itemId);
        itemOwnerNameDescAvailValidator(item, oldItem, userId);
        Item changedItem = itemRepository.updateItem(oldItem);
        log.info("Item updated" + changedItem);
        return itemMapper.toItemDto(changedItem);
    }

    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemRepository.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItem(Long id) {
        itemIdValidator(itemRepository.getItem(id));
        return itemMapper.toItemDto(itemRepository.getItem(id));
    }

    public List<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void removeItem(Long id) {
        itemIdValidator(itemRepository.getItem(id));
        itemRepository.removeItem(id);
    }

    private void itemIdValidator(Item item) {
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

    private void itemOwnerCheckValidator(User owner, Item newItem, long id) {
        if (owner == null) {
            throw new NotFoundValidationException(String.format("User with id=%d not found", id));
        } else {
            newItem.setOwner(owner);
        }
    }

    private void itemOwnerNameDescAvailValidator(Item item, Item oldItem, long userId) {
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundValidationException("User is not owner of this item!");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void userIdValidator(Long userId) {
        if (!userRepository.getAllUsers().contains(userRepository.getUser(userId))) {
            throw new NotFoundValidationException(String.format("user with id = %d not found.", userId));
        }
    }
}
