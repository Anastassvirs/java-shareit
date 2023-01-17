package ru.practicum.shareit.user.dto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("memoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Long, User> users;
    private Long numberOfUsers;
    private List<User> listUsers;

    public InMemoryUserStorage() {
        users = new HashMap();
        listUsers = new ArrayList<>();
        numberOfUsers = (long) 0;
    }

    @Override
    public List<User> findAll() {
        listUsers = new ArrayList();
        listUsers.addAll(users.values());
        return listUsers;
    }

    @Override
    public User findById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundAnythingException("Искомый пользователь не существует");
        }
    }

    @Override
    public User save(User user) {
        numberOfUsers++;
        user.setId(numberOfUsers);
        users.put(numberOfUsers, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }
}
