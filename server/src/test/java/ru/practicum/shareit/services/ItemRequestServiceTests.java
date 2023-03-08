package ru.practicum.shareit.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestMapperImpl;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTests {

    @Mock
    private ItemRequestRepository mockRepo;
    @Mock
    private UserRepository mockUserRepo;
    private final ItemRequestMapper mapper = new ItemRequestMapperImpl();
    @Mock
    private ItemRepository mockItemRepo;
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private ItemRequestService service;
    private final LocalDateTime moment = LocalDateTime.now();

    @BeforeEach
    void makeService() {
        service = new ItemRequestService(mockRepo, mockUserRepo, mapper, mockItemRepo,  itemMapper);
    }

    private final User requester = new User(1L, "user1", "@mail1.ru");
    private final User user = new User(2L, "user2", "@mail2.ru");
    private final Item item1 =
            new Item(1L, "itemName1", "itemDesc1", Boolean.TRUE, null, null);
    private final Item item2 =
            new Item(2L, "itemName2", "itemDesc2", Boolean.TRUE, null, null);
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "itemRequest1", null, moment);
    private final ItemRequest itemRequest2 = new ItemRequest(2L, "itemRequest2", null, moment);

    @Test
    void testItemRequestMapper() {
        itemRequest1.setRequester(requester);
        ItemRequestDto requestDto = mapper.toItemRequestDto(itemRequest1);
        assertThat(itemRequest1.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(itemRequest1.getCreated(), equalTo(requestDto.getCreated()));
        assertNotNull(requestDto.getItems());
        assertTrue(requestDto.getItems().isEmpty());
        ItemRequestDto requestDto1 = mapper.toItemRequestDto(itemRequest2);
        ItemRequest shortEntity = mapper.toItemRequest(requestDto1);
        assertThat(shortEntity.getDescription(), equalTo(itemRequest2.getDescription()));
    }

    @Test
    void testCreateRequest() {
        when(mockUserRepo.findById(2L)).thenReturn(Optional.of(user));
        when(mockRepo.save(Mockito.any(ItemRequest.class))).thenAnswer(i -> i.getArguments()[0]);
        ItemRequestDto requestDto = mapper.toItemRequestDto(itemRequest1);
        ItemRequestDto result = service.create(requestDto, 2L);
        assertThat(result.getDescription(), equalTo(itemRequest1.getDescription()));
    }

    @Test
    void testCreateRequestNotFound() {
        when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.create(mapper.toItemRequestDto(itemRequest1), 1L));
    }

    @Test
    void testGetAllByUser() {
        when(mockUserRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(mockRepo.findAllByRequesterId(Mockito.anyLong(), Mockito.any(Sort.class)))
                .thenReturn(addItemRequestsToList());
        when(mockItemRepo.findAllByRequestIdIn(Mockito.anyList())).thenReturn(addItemsToList());
        List<ItemRequestDto> result = service.getByOwner(1L);
        assertThat(result.size(), equalTo(2));
        assertFalse(result.get(0).getItems().isEmpty());
    }

    @Test
    void testGetAllByUserNotFound() {
        when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getByOwner(1L));
    }

    @Test
    void testGetAll() {
        when(mockUserRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(mockRepo.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(addItemRequestsToList()));
        when(mockItemRepo.findAllByRequestIdIn(Mockito.anyList())).thenReturn(addItemsToList());
        List<ItemRequestDto> result = service.getAll(1L, 0, 20);
        assertThat(result.size(), equalTo(2));
        assertFalse(result.get(0).getItems().isEmpty());
    }

    @Test
    void testGetAllNoItems() {
        when(mockUserRepo.findById(1L)).thenReturn(Optional.of(requester));
        when(mockRepo.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(addItemRequestsToList()));
        when(mockItemRepo.findAllByRequestIdIn(Mockito.anyList())).thenReturn(List.of());
        List<ItemRequestDto> result = service.getAll(1L, 0, 20);
        assertThat(result.size(), equalTo(2));
        assertTrue(result.get(0).getItems().isEmpty());
        List<ItemRequestDto> result2 = service.getAll(1L, 0, 20);
        assertThat(result2.size(), equalTo(2));
        assertTrue(result2.get(0).getItems().isEmpty());
    }

    @Test
    void testGetAllNotFound() {
        when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getAll(1L, 0, 20));
    }

    @Test
    void testGetById() {
        when(mockUserRepo.findById(1L)).thenReturn(Optional.of(new User()));
        when(mockRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest1));
        when(mockItemRepo.findAllByRequestId(Mockito.anyLong())).thenReturn(addItemsToList());
        ItemRequestDto result = service.getById(1L, 1L);
        assertThat(result.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(result.getItems().size(), equalTo(2));
    }

    @Test
    void testGetByIdNoItems() {
        when(mockUserRepo.findById(1L)).thenReturn(Optional.of(new User()));
        when(mockRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest1));
        when(mockItemRepo.findAllByRequestId(Mockito.anyLong())).thenReturn(List.of());
        ItemRequestDto result = service.getById(1L, 1L);
        assertThat(result.getDescription(), equalTo(itemRequest1.getDescription()));
        assertTrue(result.getItems().isEmpty());

        when(mockItemRepo.findAllByRequestId(Mockito.anyLong())).thenReturn(null);
        ItemRequestDto result2 = service.getById(1L, 1L);
        assertThat(result2.getDescription(), equalTo(itemRequest1.getDescription()));
        assertTrue(result2.getItems().isEmpty());
    }

    @Test
    void testGetByIdUserNotFound() {
        when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getById(1L, 1L));
    }

    @Test
    void testGetByIdRequestNotFound() {
        when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        when(mockRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getById(1L, 1L));
    }

    private List<ItemRequest> addItemRequestsToList() {
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);
        itemRequests.add(itemRequest2);
        return itemRequests;
    }

    private List<Item> addItemsToList() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        item1.setRequestId(itemRequest1.getId());
        items.add(item2);
        item2.setRequestId(itemRequest2.getId());
        return items;
    }
}
