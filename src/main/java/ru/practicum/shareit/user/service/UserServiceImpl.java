package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.SameFieldException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundAnythingException("Пользователя с данным id не существует"));
    }

    @Transactional
    @Override
    public User createUser(User user) {
        if (userAlreadyExist(user)) {
            log.debug("Произошла ошибка: Введенный пользователь уже зарегистрирован");
            throw new AlreadyExistException("Такой пользователь уже зарегистрирован");
        }
        log.debug("Добавлен новый пользователь: {}", user);
        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = findById(userId);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        if (userDto.getEmail() != null) {
            if (!emailAlreadyExist(userDto.getEmail())) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new SameFieldException("Данный email уже зарегистрирован");
            }
        }
        if (userAlreadyExist(user)) {
            log.debug("Обновлен пользователь: {}", user);
            return repository.save(user);
        } else {
            log.debug("Произошла ошибка: Введенного пользователя не существует");
            throw new NotFoundAnythingException("Такого пользователя не существует");
        }
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private boolean emailAlreadyExist(String email) {
        for (User oldUser : repository.findAll()) {
            if (Objects.equals(oldUser.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    private boolean userAlreadyExist(User user) {
        for (User oldUser : repository.findAll()) {
            if (Objects.equals(oldUser.getId(), user.getId())) {
                return true;
            }
        }
        return false;
    }
}
