package ru.practicum.shareit.item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
=======
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
<<<<<<< HEAD
import java.util.Collections;
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
<<<<<<< HEAD
@Validated
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
<<<<<<< HEAD
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody CreateItemRequest itemDto,
=======
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(201).body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
<<<<<<< HEAD
    public ResponseEntity<ItemDto> updateItem(@RequestBody CreateItemRequest itemDto, @PathVariable Long itemId,
=======
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.updateItem(itemDto, itemId, userId));
    }

    @GetMapping("/search")
<<<<<<< HEAD
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String text,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        if (text.isBlank()) return ResponseEntity.ok().body(Collections.emptyList());
=======
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String text,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
        return ResponseEntity.ok().body(itemService.search(text, from, size));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        itemService.removeItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return ResponseEntity.ok().body(itemService.getItem(itemId, requesterId));
    }

<<<<<<< HEAD
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemService.getAllByOwnerId(ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
=======
    @GetMapping()
    public ResponseEntity<List<ItemDto>> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemService.getAllItemsByUserId(userId));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    public ResponseEntity<CommentDto> createComment(@PathVariable Long itemId,
                                    @RequestBody @Valid CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return ResponseEntity.ok().body(itemService.createComment(itemId, commentDto, authorId));
    }
}