package ru.practicum.shareit.request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRequestMapper mapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public ItemRequestDto create(ItemRequestDto request, Long userId) {
        User user = findUser(userId);
        ItemRequest itemRequest = mapper.toItemRequest(request);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        log.debug("New item request + :" + itemRequest.getDescription() + " save");
        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getByOwner(Long ownerId) {
        findUser(ownerId);
        Sort sort = Sort.by("created").ascending();
        return addItems(mapper.toListDto(repository.findAllByRequesterId(ownerId, sort)));
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        findUser(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by("created").ascending());
        List<ItemRequestDto> requests =
                mapper.toListDto(repository.findAllByRequesterIdNot(userId, page).getContent());
        return addItems(requests);
    }

    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long userId, Long requestId) {
        findUser(userId);
        ItemRequest request = repository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundValidationException("Request with id: " + requestId + " not found"));
        return addItem(mapper.toItemRequestDto(request));
    }

    private User findUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundValidationException("User with id: " + userId + " not found"));
    }

    private List<ItemRequestDto> addItems(List<ItemRequestDto> requests) {
        List<Long> requestsId = requests.stream().map(ItemRequestDto::getId).collect(toList());
        List<ItemDto> items = itemMapper.toDtoList(itemRepository.findAllByRequestIdIn(requestsId));
        Map<Long, List<ItemDto>> itemsMap = items.stream().collect(groupingBy(ItemDto::getRequestId, toList()));
        if (items.isEmpty()) {
            return requests;
        } else {
            return requests.stream().peek(r -> r.setItems(itemsMap.getOrDefault(r.getId(), Collections.emptyList())))
                    .collect(toList());
        }
    }

    private ItemRequestDto addItem(ItemRequestDto request) {
        List<ItemDto> items = itemMapper.toDtoList(itemRepository.findAllByRequestId(request.getId()));
        if (items != null && !items.isEmpty()) {
            request.setItems(items);
        }
        return request;
    }
}
