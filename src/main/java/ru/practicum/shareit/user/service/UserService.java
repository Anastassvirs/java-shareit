package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User createUser(UserDto user);

    User updateUser(Long userId, UserDto userDto);

    void deleteById(Long id);

    boolean emailAlreadyExist(String email);

    boolean userExistById(Long id);
}
