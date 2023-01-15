package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User find(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(path = "/{userId}", consumes = "application/json")
    public ResponseEntity<User> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        User user = userService.findById(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!userService.emailAlreadyExist(userDto.getEmail())) {
                user.setEmail(userDto.getEmail());
            } else {
                throw new ValidationException("Данный email уже зарегистрирован");
            }
        }
        userService.updateUser(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}