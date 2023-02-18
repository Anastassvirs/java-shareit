package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository repository, UserMapper userMapper) {
        this.repository = repository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundAnythingException("Пользователя с данным id не существует"));
    }

    @Transactional
    @Override
    public User createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (Objects.isNull(userDto.getEmail()) || !userDto.getEmail().contains("@") ||
                Objects.isNull(userDto.getName()) || userDto.getName().isEmpty() || userDto.getEmail().isEmpty()) {
            throw new WrongParametersException("Неправильно заполнены поля создаваемого пользователя");
        }
        log.debug("Добавлен новый пользователь: {}", user);
        try {
            return repository.save(user);
        } catch (Exception e) {
            log.debug("Произошла ошибка: Неправильно заполнены поля создаваемого пользователя");
            throw new SaveUserException("Неправильно заполнены поля создаваемого пользователя");
        }
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = findById(userId);
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        if (userDto.getEmail() != null) {
            if (!emailAlreadyExist(userDto.getEmail())) {
                try {
                    user.setEmail(userDto.getEmail());
                } catch (Exception e) {
                    throw new SameFieldException("Данный email уже зарегистрирован");
                }
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

    @Transactional
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

    public boolean userExistById(Long id) {
        for (User oldUser : repository.findAll()) {
            if (Objects.equals(oldUser.getId(), id)) {
                return true;
            }
        }
        return false;
    }
}
