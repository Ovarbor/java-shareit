package ru.practicum.shareit.item.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }


    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByDescription(text);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        itemService.removeItem(itemId);
    }
}