package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User findById(Long id);

    User save(User user);

    User update(User user);

    void delete(Long id);
}
