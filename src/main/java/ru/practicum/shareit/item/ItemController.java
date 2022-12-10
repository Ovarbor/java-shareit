package ru.practicum.shareit.item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemCreated = itemService.createItem(itemDto, userId);
        return ResponseEntity.status(201).body(itemCreated);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto itemUpdated = itemService.updateItem(itemDto, itemId, userId);
        return ResponseEntity.ok().body(itemUpdated);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String text,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        List<ItemDto> searchItems = itemService.search(text, from, size);
        return ResponseEntity.ok().body(searchItems);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        itemService.removeItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        ItemDto item = itemService.getItem(itemId, requesterId);
        return ResponseEntity.ok().body(item);
    }

    @GetMapping()
    public ResponseEntity<List<ItemDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> items = itemService.getAllItemsByUserId(userId);
        return ResponseEntity.ok().body(items);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentDto> createComment(@PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long authorId) {
        CommentDto comment = itemService.createComment(itemId, commentDto, authorId);
        return ResponseEntity.ok().body(comment);
    }
}