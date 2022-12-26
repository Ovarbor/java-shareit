package ru.practicum.shareit.request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long requesterId, Sort sort);

    Page<ItemRequest> findAllByRequesterIdNot(Long requesterId, Pageable pageable);
}
