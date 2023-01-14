package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestStatus;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.IllegalRequestException;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.ItemRepository;
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

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {

    @Mock
    private BookingRepository mockBookingRepo;
    @Mock
    private ItemRepository mockItemRepo;
    @Mock
    private UserRepository mockUserRepo;
    private BookingService service;

    private final BookingMapper mapper = new BookingMapperImpl();
    private final LocalDateTime moment = LocalDateTime.now();

    private final User owner = new User(1L, "user1", "@mail1.ru");
    private final User booker = new User(2L, "user2", "@mail2.ru");
    private final Booking booking1 =
            new Booking(1L, moment.plusSeconds(1), moment.plusHours(1), null, null, Status.WAITING);
    private final Booking booking2 =
            new Booking(2L, moment.plusSeconds(2), moment.plusHours(2), null, null, Status.WAITING);
    private final Item item =
            new Item(1L, "item", "itemDesc", Boolean.TRUE, owner, null);
    private final BookingRequest request = new BookingRequest(1L, moment, moment.plusDays(1));

    @BeforeEach
    void createService() {
        service = new BookingService(mockBookingRepo, mapper, mockUserRepo, mockItemRepo);
    }

    @Test
    void testCreateBooking() {
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(mockBookingRepo.save(Mockito.any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);
        BookingDto result = service.create(request, 2L);
        assertThat(result.getStart(), equalTo(request.getStart()));
        assertThat(result.getEnd(), equalTo(request.getEnd()));
        assertThat(result.getItemId(), equalTo(request.getItemId()));
        assertThat(result.getBooker().getName(), equalTo(booker.getName()));
        assertThat(result.getItem().getName(), equalTo(item.getName()));
        assertThat(result.getBookerId(), equalTo(2L));
        assertThat(result.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void testCreateBookingItemNotFound() {
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.create(request, 3L));
    }

    @Test
    void testCreateBookingItemIsNotAvailable() {
        item.setAvailable(Boolean.FALSE);
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        assertThrows(IllegalRequestException.class, () -> service.create(request, 4L));
    }

    @Test
    void testCreateBookingSelfItem() {
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundValidationException.class, () -> service.create(request, 49L));
    }

    @Test
    void testCreateBookingRequesterNotFound() {
        Mockito.when(mockItemRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.create(request, 5L));
    }

    @Test
    void testChangeStatusByOwnerApproved() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.WAITING);
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        BookingDto result = service.changeStatusByOwner(1L, Boolean.TRUE, 1L);
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void testChangeStatusByOwnerRejected() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.WAITING);
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        BookingDto result = service.changeStatusByOwner(1L, Boolean.FALSE, 1L);
        assertThat(result.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void testChangeStatusBookingNotFound() {
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.changeStatusByOwner(1L, Boolean.TRUE, 1L));
    }

    @Test
    void testChangeStatusUserNotOwner() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.WAITING);
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundValidationException.class, () ->
                service.changeStatusByOwner(1L, Boolean.TRUE, 2L));
    }

    @Test
    void testChangeStatusAlreadyApproved() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.APPROVED);
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        assertThrows(IllegalRequestException.class, () -> service
                .changeStatusByOwner(1L, Boolean.TRUE, 1L));
    }

    @Test
    void testGetById() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.WAITING);
        Mockito.when(mockBookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        BookingDto result1 = service.getById(1L, 1L);
        assertThat(result1.getItem().getName(), equalTo(item.getName()));
        assertThat(result1.getStatus(), equalTo(booking.getStatus()));
        BookingDto result2 = service.getById(1L, 1L);
        assertEquals(result1, result2);
    }

    @Test
    void testGetByIdNotFound() {
        Mockito.when(mockBookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () -> service.getById(1L, 1L));
    }

    @Test
    void testGetByIdNotBookerOrOwner() {
        Booking booking = new Booking(1L, moment, moment.plusDays(1), item, booker, Status.WAITING);
        Mockito.when(mockBookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(NotFoundValidationException.class, () -> service.getById(1L, 3L));
    }

    @Test
    void testGetAllByUsers() {
        List<BookingDto> plannedList = mapper.toBookingDtoList(addToBookingList());
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        Mockito.when(mockBookingRepo.findByBookerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByBookerIdAndEndIsBefore(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByBookerIdAndStartIsAfter(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByBookerIdAndStatusEquals(Mockito.anyLong(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        assertThat(service.getAllByUser(RequestStatus.ALL, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUser(RequestStatus.CURRENT, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUser(RequestStatus.PAST, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUser(RequestStatus.FUTURE, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUser(RequestStatus.WAITING, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUser(RequestStatus.REJECTED, 1L, 0, 20), equalTo(plannedList));
    }

    @Test
    void testGetAllByUsersBookerNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.getAllByUser(RequestStatus.ALL, 1L, 0, 20));
    }

    @Test
    void testGetAllByUserOwner() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booker));
        List<BookingDto> plannedList = mapper.toBookingDtoList(addToBookingList());
        List<Item> itemsList = List.of(new Item(), new Item());
        int i = 0;
        for (Item item : itemsList) {
            item.setId((long) i++);
        }
        Mockito.when(mockItemRepo.findByOwnerIdOrderById(Mockito.any())).thenReturn(itemsList);
        Mockito.when(mockBookingRepo.findByItemIdIn(Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByItemIdInAndStartIsBeforeAndEndIsAfter(Mockito.any(),
                        Mockito.any(), Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByItemIdInAndEndIsBefore(Mockito.any(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByItemIdInAndStartIsAfter(Mockito.any(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        Mockito.when(mockBookingRepo.findByItemIdInAndStatusEquals(Mockito.any(),
                        Mockito.any(), Mockito.any(Pageable.class)))
                .thenReturn(addToBookingList());
        assertThat(service.getAllByUserOwner(RequestStatus.ALL, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUserOwner(RequestStatus.CURRENT, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUserOwner(RequestStatus.PAST, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUserOwner(RequestStatus.FUTURE, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUserOwner(RequestStatus.WAITING, 1L, 0, 20), equalTo(plannedList));
        assertThat(service.getAllByUserOwner(RequestStatus.REJECTED, 1L, 0, 20), equalTo(plannedList));
    }

    @Test
    void testGetAllByUsersOwnerNotFound() {
        Mockito.when(mockUserRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundValidationException.class, () ->
                service.getAllByUserOwner(RequestStatus.ALL, 1L, 0, 20));
    }

    @Test
    void testBookingMapper() {
        BookingDto bookingDto = mapper.toBookingDto(booking1);
        Booking returnBooking = mapper.toBooking(bookingDto);
        assertThat(booking1, equalTo(returnBooking));
        List<Booking> bookings = addToBookingList();
        List<BookingDtoShort> shortBookings = mapper.toDtoShortList(bookings);
        assertThat(shortBookings.size(), equalTo(bookings.size()));
        assertNull(mapper.toDtoShortList(null));
        BookingDtoItem miniItem = mapper.toBookingDtoItem(item);
        assertThat(miniItem.getId(), equalTo(item.getId()));
        assertThat(miniItem.getName(), equalTo(item.getName()));
        BookingDtoBooker miniBooker = mapper.toBookingDtoBooker(booker);
        assertThat(miniBooker.getId(), equalTo(booker.getId()));
        assertThat(miniBooker.getName(), equalTo(booker.getName()));
        bookingDto.setBooker(miniBooker);
        bookingDto.setItem(miniItem);
        Booking newBooking = mapper.toBooking(bookingDto);
        assertThat(newBooking.getItem().getName(), equalTo(miniItem.getName()));
        assertThat(newBooking.getBooker().getName(), equalTo(miniBooker.getName()));
    }

    private List<Booking> addToBookingList() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);
        return bookings;
    }
}
