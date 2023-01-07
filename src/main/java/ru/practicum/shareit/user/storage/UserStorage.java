package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Long id);

    User saveUser(User user);

    User updateUser(User user);

    User deleteUser(User user);
}
