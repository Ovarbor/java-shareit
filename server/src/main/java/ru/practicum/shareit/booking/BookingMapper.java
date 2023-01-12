package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    Booking toBooking(BookingDto bookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoShort toDtoShort(Booking booking);

    List<BookingDtoShort> toDtoShortList(List<Booking> bookings);

    @Mapping(target = "item.id", source = "itemId")
    Booking toBookingFromRequest(BookingRequest request);

    List<BookingDto> toBookingDtoList(List<Booking> bookings);

    BookingDtoBooker toBookingDtoBooker(User booker);

    BookingDtoItem toBookingDtoItem(Item item);
}
