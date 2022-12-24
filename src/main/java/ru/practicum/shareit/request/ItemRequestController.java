package ru.practicum.shareit.request;
<<<<<<< HEAD
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
=======
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO Sprint add-item-requests.
 */
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    @Autowired
    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestBody @Validated ItemRequestDto request,
                                 @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return ResponseEntity.ok().body(service.create(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getByUser(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return ResponseEntity.ok().body(service.getByOwner(userId));
    }

    @GetMapping("/all")
    public ResponseEntity <List<ItemRequestDto>> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return ResponseEntity.ok().body(service.getAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                  @PathVariable("requestId") @Positive Long requestId) {
        return ResponseEntity.ok().body(service.getById(userId, requestId));
    }
}
