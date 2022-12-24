package ru.practicum.shareit.item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

<<<<<<< HEAD
    List<Item> findByOwnerId(Long ownerId, Pageable pageable);

=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
    List<Item> findByOwnerId(Long ownerId);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            "and (i.available) is true")
    Page<Item> search(String text, Pageable pageable);
<<<<<<< HEAD

    List<Item> findAllByRequestIdIn(List<Long> requestsId);

    List<Item> findAllByRequestId(Long id);
=======
>>>>>>> bb4082fcd0f4558ce93b4e2a8023a6df1366e0fe
}
