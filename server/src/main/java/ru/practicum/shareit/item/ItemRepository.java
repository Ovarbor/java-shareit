package ru.practicum.shareit.item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {

    List<Item> findByOwnerIdOrderById(Long ownerId, Pageable pageable);

    List<Item> findByOwnerIdOrderById(Long ownerId);

    @Query("select item from Item item " +
            "where (upper(item.name) like upper(concat('%', ?1, '%')) " +
            "or upper(item.description) like upper(concat('%', ?1, '%')))" +
            "and (item.available) is true ORDER BY item.id")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requestsId);

    List<Item> findAllByRequestId(Long id);
}
