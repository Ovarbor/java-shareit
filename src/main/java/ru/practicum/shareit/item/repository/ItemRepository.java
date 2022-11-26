package ru.practicum.shareit.item.repository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository {

        Item createItem(Item item);

        Item updateItem(Item item);

        Item getItem(Long id);

        List<Item> getAllItems();

        void removeItem(Long id);
}
