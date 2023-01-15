package ru.practicum.shareit.user;

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
        if (emailAlreadyExist(user.getEmail())) {
            log.debug("Произошла ошибка валидации");
            throw new ValidationException("Произошла ошибка валидации");
        }
        log.debug("Добавлен новый пользователь: {}", user);
        return storage.save(user);
    }

    public User updateUser(User user) {
        if (userAlreadyExist(user)) {
            log.debug("Обновлен пользователь: {}", user);
            return storage.update(user);
        } else {
            log.debug("Произошла ошибка: Введенного пользователя не существует");
            throw new NotFoundAnythingException("Такого пользователя не существует");
        }
    }

    public void deleteById(Long id) {
        storage.delete(id);
    }

    public boolean emailAlreadyExist(String email) {
        for (User oldUser: storage.findAll()) {
            if (Objects.equals(oldUser.getEmail(), email)) {
                return true;
            }
        }
        return false;
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
