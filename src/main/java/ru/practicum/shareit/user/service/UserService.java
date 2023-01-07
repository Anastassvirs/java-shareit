package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("memoryUserStorage") UserStorage storage) {
        this.storage = storage;
    }

    public List<User> findAll() {
        return storage.findAll();
    }

    public User findById(Long id) {
        return storage.findById(id);
    }

    public User createUser(User user) {
        if (userAlreadyExist(user)) {
            log.debug("Произошла ошибка: Введенный пользователь уже зарегистрирован");
            throw new AlreadyExistException("Такой пользователь уже зарегистрирован");
        }
        if (!validate(user)) {
            log.debug("Произошла ошибка валидации");
            throw new AlreadyExistException("Произошла ошибка валидации");
        }
        log.debug("Добавлен новый пользователь: {}", user);
        return storage.saveUser(user);
    }

    public User updateUser(User user) {
        if(validate(user)) {
            if (userAlreadyExist(user)) {
                log.debug("Обновлен пользователь: {}", user);
                return storage.updateUser(user);
            } else {
                log.debug("Произошла ошибка: Введенного пользователя не существует");
                throw new NotFoundAnythingException("Такого пользователя не существует");
            }
        } else {
            throw new ValidationException("Произошла ошибка валидации");
        }
    }

    public User deleteUser(User user) {
        return storage.deleteUser(user);
    }

    private boolean validate(User user) throws ValidationException {

        for (User oldUser: storage.findAll()) {
            if (Objects.equals(oldUser.getEmail(), user.getEmail())) {
                return false;
            }
        }
        return true;
    }

    private boolean userAlreadyExist(User user) {
        for (User oldUser: storage.findAll()) {
            if (Objects.equals(oldUser.getId(), user.getId())) {
                return true;
            }
        }
        return false;
    }
}
