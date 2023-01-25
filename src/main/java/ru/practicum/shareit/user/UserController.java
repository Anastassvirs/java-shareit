package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public List<User> findAll() {
        return userServiceImpl.findAll();
    }

    @GetMapping("/{userId}")
    public User find(@PathVariable Long userId) {
        return userServiceImpl.findById(userId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userServiceImpl.createUser(user);
    }

    @PatchMapping(path = "/{userId}", consumes = "application/json")
    public ResponseEntity<User> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userServiceImpl.updateUser(userId, userDto));
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteById(@PathVariable Long userId) {
        userServiceImpl.deleteById(userId);
    }
}