package ru.practicum.shareit.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private ItemRepository mockItemRepo;
    @Mock
    private UserRepository mockUserRepo;
    ItemMapper itemMapper = new ItemMapperImpl();
    @Mock
    private BookingRepository mockBookingRepo;
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();
    @Mock
    private CommentRepository mockCommentRepo;
    private ItemService service;
    private final LocalDateTime moment = LocalDateTime.now();

    @BeforeEach
    void makeService() {
        service = new ItemService(mockUserRepo, mockItemRepo, itemMapper, mockBookingRepo,
                bookingMapper, mockCommentRepo, commentMapper);
    }

    private final User owner = new User(1L, "user1", "@mail1.ru");
    private final User user = new User(2L, "user2", "@mail2.ru");
    private final CreateItemRequest itemRequest1 =
            new CreateItemRequest(1L, "requestName1", "requestDesc1", Boolean.TRUE, null);
    private final CreateItemRequest itemRequest2 =
            new CreateItemRequest(2L, "requestName2", "requestDesc2", Boolean.TRUE, null);
    private final Item item1 =
            new Item(1L, "itemName1", "itemDesc1", Boolean.TRUE, null, null);
    private final Item item2 =
            new Item(2L, "itemName2", "itemDesc2", Boolean.TRUE, null, null);
    private final Item item3 =
            new Item(3L, "itemName3", "itemDesc3", Boolean.TRUE, null, null);
    private final Comment comment1 = new Comment(1L, "comment1", item1, null, LocalDateTime.now());
    private final Comment comment2 = new Comment(2L, "comment2", item2, null, LocalDateTime.now());
    private final Comment comment3 = new Comment(3L, "comment3", item3, null, LocalDateTime.now());
    private final Booking booking1 =
            new Booking(1L, moment.minusDays(1), moment.plusHours(2), item1, null, Status.WAITING);
    private final Booking booking2 =
            new Booking(2L, moment.minusDays(2), moment.plusHours(4), item1, null, Status.WAITING);

    @Test
    void testItemMapper() {
        Item item = itemMapper.toItem(itemRequest1);
        assertThat(itemRequest1.getId(), equalTo(item.getId()));
        assertThat(itemRequest1.getName(), equalTo(item.getName()));
        assertThat(itemRequest1.getDescription(), equalTo(item.getDescription()));
        assertThat(itemRequest1.getAvailable(), equalTo(item.getAvailable()));
        assertNull(item.getRequestId());
        ItemDto itemDto2 = itemMapper.toItemDto(item2);
        assertThat(item2.getId(), equalTo(itemDto2.getId()));
        assertThat(item2.getName(), equalTo(itemDto2.getName()));
        assertThat(item2.getAvailable(), equalTo(itemDto2.getAvailable()));
        assertNull(itemDto2.getLastBooking());
        assertNull(itemDto2.getNextBooking());
        assertNull(itemDto2.getComments());
        assertThat(item2, equalTo(itemMapper.toItem(itemDto2)));
    }

    @Test
    void testCreateItem() {
        Mockito.when(mockUserRepo.findById(1L)).thenReturn(Optional.of(owner));
        Mockito.when(mockItemRepo.save(Mockito.any(Item.class))).thenAnswer(i -> i.getArguments()[0]);
        ItemDto result = service.createItem(itemRequest1, 1L);
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo(itemRequest1.getName()));
    }

    @Test
    void testCreateItemOwnerNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.createItem(itemRequest1, 1L));
    }

    @Test
    void testPatchItem() {
        item1.setOwner(owner);
        Mockito.when(mockItemRepo.findById(1L)).thenReturn(Optional.of(item1));
        CreateItemRequest newItem = itemRequest2;
        newItem.setId(1L);
        newItem.setAvailable(Boolean.FALSE);
        ItemDto result = service.updateItem(newItem, 1L, 1L);
        assertThat(result.getName(), equalTo(newItem.getName()));
        assertThat(result.getDescription(), equalTo(newItem.getDescription()));
        assertThat(result.getAvailable(), equalTo(newItem.getAvailable()));
    }

    @Test
    void testPatchItemNotFound() {
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.updateItem(itemRequest1, 1L, 1L));
    }

    @Test
    void testPatchItemForbidden() {
        item1.setOwner(owner);
        Mockito.when(mockItemRepo.findById(1L)).thenReturn(Optional.of(item1));
        assertThrows(NotFoundValidationException.class, () ->
                service.updateItem(itemRequest1, 1L, 2L));
    }

    @Test
    void testGetItem() {
        item1.setOwner(owner);
        Mockito.when(mockItemRepo.findById(1L)).thenReturn(Optional.of(item1));
        Mockito.when(mockCommentRepo.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(addCommentsToList());
        ItemDto result = service.getItem(1L, 2L);
        assertThat(item1.getName(), equalTo(result.getName()));
        assertThat(result.getComments().size(), equalTo(3));
        assertNull(result.getNextBooking());
        assertNull(result.getLastBooking());
    }

    @Test
    void testGetItemByOwner() {
        item1.setOwner(owner);
        Mockito.when(mockItemRepo.findById(1L)).thenReturn(Optional.of(item1));
        Mockito.when(mockCommentRepo.findAllByItemIdOrderByCreatedDesc(1L)).thenReturn(addCommentsToList());
        Mockito.when(mockBookingRepo.findByItemIdAndEndIsBefore(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(mockBookingRepo.findByItemIdAndStartIsAfter(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Optional.of(booking2));
        ItemDto result = service.getItem(1L, item1.getOwner().getId());
        assertThat(item1.getName(), equalTo(result.getName()));
        assertThat(result.getComments().size(), equalTo(3));
        assertThat(result.getLastBooking().getStart(), equalTo(moment.minusDays(1)));
        assertThat(result.getNextBooking().getEnd(), equalTo(moment.plusHours(2 * 2)));
    }

    @Test
    void testGetItemNotFound() {
        Mockito.when(mockItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getItem(1L, 1L));
    }

    @Test
    void testGetAllByOwner() {
        item1.setOwner(owner);
        item2.setOwner(owner);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        Mockito.when(mockUserRepo.findById(1L)).thenReturn(Optional.of(owner));
        Mockito.when(mockItemRepo.findByOwnerIdOrderById(Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(items);
        List<ItemDto> result = service.getAllByOwnerId(owner.getId(), 0, 20);
        assertThat(result.size(), equalTo(2));
    }

    @Test
    void testGetAllByOwnerNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getAllByOwnerId(1L, 0, 20));
    }

    @Test
    void testSearch() {
        Mockito.when(mockItemRepo.search(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(addItemsToList()));
        assertFalse(service.search("text", 0, 20).isEmpty());
    }

    @Test
    void deleteItem() {
        service.removeItem(item1.getId());
        verify(mockItemRepo, Mockito.times(1)).deleteById(item1.getId());
    }

    @Test
    void testCreateComment() {
        Mockito.when(mockUserRepo.findById(1L)).thenReturn(Optional.of(user));
        addBookingsToList().get(0).setStatus(Status.APPROVED);
        Mockito.when(mockBookingRepo.findAllByBookerIdAndItemIdAndEndIsBefore(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any())).thenReturn(addBookingsToList());
        item1.setOwner(owner);
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(item1));
        Mockito.when(mockCommentRepo.save(Mockito.any(Comment.class))).thenAnswer(i -> i.getArguments()[0]);
        CommentDto result = service.createComment(1L, commentMapper.toCommentDto(comment1), 1L);
        assertThat(result.getAuthorName(), equalTo(user.getName()));
        assertThat(result.getItemId(), equalTo(item1.getId()));
        assertEquals(commentMapper.toComment(result), comment1);
    }

    private List<Item> addItemsToList() {
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        return items;
    }

    private List<Comment> addCommentsToList() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);
        return comments;
    }

    private List<Booking> addBookingsToList() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);
        return bookings;
    }


}
