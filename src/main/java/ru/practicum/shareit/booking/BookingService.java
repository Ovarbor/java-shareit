package ru.practicum.shareit.booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestStatus;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.IllegalRequestException;
import ru.practicum.shareit.exceptions.NotFoundValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDto create(BookingRequest request, Long requesterId) {
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (item.isEmpty()) throw new NotFoundValidationException("Item not found.");
        if (item.get().getOwner().getId().equals(requesterId))
            throw new NotFoundValidationException("Booking your item? Why?");
        if (item.get().getAvailable().equals(Boolean.FALSE))
            throw new IllegalRequestException("This item not available.");
        if (!request.getStart().isBefore(request.getEnd()))
            throw new IllegalRequestException("Booking start is after the end.");
        Booking booking = bookingMapper.toBookingFromRequest(request);
        booking.setItem(item.get());
        Optional<User> requester = userRepository.findById(requesterId);
        if (requester.isEmpty()) throw new NotFoundValidationException("Requester not found by ID.");
        booking.setBooker(requester.get());
        booking.setStatus(Status.WAITING);
        log.info("Booking created: {}", booking);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDto changeStatusByOwner(Long bookingId, Boolean approved, Long ownerId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) throw new NotFoundValidationException("Booking with id: " + bookingId + " not found.");
        User owner = booking.get().getItem().getOwner();
        if (!owner.getId().equals(ownerId)) throw new NotFoundValidationException("This is not user's item.");
        if (booking.get().getStatus().equals(Status.APPROVED))
            throw new IllegalRequestException("Booking already approved");
        if (approved.equals(Boolean.TRUE)) {
            log.info("Approve booking id:{}", bookingId);
            booking.get().setStatus(Status.APPROVED);
        } else {
            log.info("Reject booking id:{}", bookingId);
            booking.get().setStatus(Status.REJECTED);
        }
        return bookingMapper.toBookingDto(booking.get());
    }

    @Transactional(readOnly = true)
    public BookingDto getById(Long bookingId, Long userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) throw new NotFoundValidationException("Booking with id: " + bookingId + " not found.");

        if (!booking.get().getBooker().getId().equals(userId)
                && !booking.get().getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundValidationException("This is not user's booking or item.");
        }
        return bookingMapper.toBookingDto(booking.get());
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUser(String state, Long userId, Integer from, Integer size) {
        if (userRepository.findById(userId).isEmpty())
            throw new NotFoundValidationException("Requester with id: " + userId + " not found");
        RequestStatus requestStatus = RequestStatus.parseState(state);
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (requestStatus) {
            case ALL:
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerId(userId, page));
            case CURRENT:
                LocalDateTime moment = LocalDateTime.now();
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        moment, moment, page));
            case PAST:
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), page));
            case FUTURE:
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), page));
            case WAITING:
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStatusEquals(userId,
                        Status.WAITING, page));
            case REJECTED:
                return bookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStatusEquals(userId,
                        Status.REJECTED, page));
            default:
                throw new IllegalRequestException("Status is wrong");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUserOwner(String state, Long userId, Integer from, Integer size) {
        if (userRepository.findById(userId).isEmpty())
            throw new NotFoundValidationException("Requester with id: " + userId + " not found");
        RequestStatus requestStatus = RequestStatus.parseState(state);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<Long> userItemsIds = userItems.stream().map(Item::getId).collect(Collectors.toList());
        Pageable page = PageRequest.of(from / size, size, Sort.by("start").descending());
        switch (requestStatus) {
            case ALL:
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdIn(userItemsIds, page));
            case CURRENT:
                LocalDateTime moment = LocalDateTime.now();
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(
                        userItemsIds, moment, moment, page));
            case PAST:
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndEndIsBefore(
                        userItemsIds, LocalDateTime.now(), page));
            case FUTURE:
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStartIsAfter(
                        userItemsIds, LocalDateTime.now(), page));
            case WAITING:
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStatusEquals(
                        userItemsIds, Status.WAITING, page));
            case REJECTED:
                return bookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStatusEquals(
                        userItemsIds, Status.REJECTED, page));
            default:
                throw new NotFoundValidationException("Status is wrong");
        }
    }
}
