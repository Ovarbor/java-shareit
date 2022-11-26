package ru.practicum.shareit.user.repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User createUser(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }
}
