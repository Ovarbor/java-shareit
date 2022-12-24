package ru.practicum.shareit.booking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStatusEquals(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByItemIdIn(List<Long> items, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> items,
                                                              LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> items, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> items, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemIdInAndStatusEquals(List<Long> items, Status status, Pageable pageable);

    Optional<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime end);

    Optional<Booking> findByItemIdAndStartIsAfter(Long itemId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);
}
