package ru.practicum.shareit.item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingUpdate;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return itemClient.getItemById(userId, id);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @Valid @RequestBody ItemBookingUpdate itemDto,
                                             @PathVariable Long itemId) {
        return itemClient.updateItem(id, itemDto, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsDtoOfUser(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getAllItemsDtoOfUser(id, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByNameOrDescription(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItemsByNameOrDescription(id, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemClient.createComment(id, itemId, commentDto);
    }
}
