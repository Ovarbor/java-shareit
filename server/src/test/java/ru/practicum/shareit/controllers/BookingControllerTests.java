package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingMapperImpl;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBooker;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final LocalDateTime moment = LocalDateTime.now();
    private final BookingRequest bookingRequest1 = new BookingRequest(1L, moment.plusHours(1), moment.plusDays(1));
    private final BookingRequest bookingRequest2 = new BookingRequest(2L, moment.plusHours(2), moment.plusDays(2));
    private final BookingDto bookingDto1 = new BookingDto(1L, moment.plusHours(1), moment.plusDays(1),
            new BookingDtoItem(1L, null), new BookingDtoBooker(2L, null), null);

    @Test
    void testCheckBookingMapper() {
        bookingDto1.setId(null);
        bookingDto1.setBooker(null);
        assertEquals(bookingDto1, requestToDto(bookingRequest1));
        Booking entity = bookingMapper.toBooking(bookingDto1);
        User booker = new User();
        booker.setId(2L);
        entity.setBooker(booker);
        entity.getBooker().setId(null);
    }

    @Test
    void testCreateBooking() throws Exception {
        when(service.create(Mockito.any(BookingRequest.class), Mockito.anyLong())).thenReturn(bookingDto1);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequest1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id", is(1L), Long.class));
    }

    @Test
    void testSetStatusByOwner() throws Exception {
        bookingDto1.setStatus(Status.APPROVED);
        when(service.changeStatusByOwner(Mockito.anyLong(),
                Mockito.anyBoolean(), Mockito.anyLong())).thenReturn(bookingDto1);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class));
    }

    @Test
    void testSetStatusByOwnerAlreadyApproved() throws Exception {
        when(service.changeStatusByOwner(Mockito.anyLong(),
                Mockito.anyBoolean(), Mockito.anyLong())).thenThrow(NotFoundValidationException.class);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));

    }

    @Test
    void testGetById() throws Exception {
        when(service.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto1);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAllByUser(Mockito.any(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(addToBookingDtoList());
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void testGetAllBadState() throws Exception {
        when(service.getAllByUser(Mockito.any(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenThrow(NotFoundValidationException.class);
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "RYHNSKDN")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        when(service.getAllByUserOwner(Mockito.any(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(addToBookingDtoList());
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    private List<BookingDto> addToBookingDtoList() {
        List<BookingDto> bookingDtos = new ArrayList<>();
        bookingDtos.add(bookingDto1);
        return bookingDtos;
    }

    private BookingDto requestToDto(BookingRequest request) {
        return bookingMapper.toBookingDto(bookingMapper.toBookingFromRequest(request));
    }
}
