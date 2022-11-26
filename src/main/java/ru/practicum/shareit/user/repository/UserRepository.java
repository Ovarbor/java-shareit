package ru.practicum.shareit.user.repository;
import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    User getUser(Long id);

    List<User> getAllUsers();

    void removeUser(Long id);
}
