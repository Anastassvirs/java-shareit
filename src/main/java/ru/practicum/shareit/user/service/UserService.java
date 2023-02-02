package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User createUser(UserDto user);

    User updateUser(Long userId, UserDto userDto);

    void deleteById(Long id);

    boolean userExistById(Long id);
}
