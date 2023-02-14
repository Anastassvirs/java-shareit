package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exceptions.SameFieldException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Autowired
    ObjectMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void findAllTest() {
        List<User> users = List.of(new User("Anastasia", "an.svir@mail.com"),
                new User("NEAnastasia", "nean.svir@mail.com"),
                new User("HEHESir", "phphphph.sir@mail.com")
        );
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(users, userService.findAll());
    }

    @Test
    public void findByIdTest() {
        Long userId = 1L;
        User user = new User(userId, "Anastasia", "an.svir@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.findById(userId));
    }

    @Test
    public void saveTest() {
        UserDto userDto = new UserDto("Anastasia", "an.svir@mail.com");
        User user = new User("Anastasia", "an.svir@mail.com");
        user.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertEquals(user, userService.createUser(userDto));
    }

    @Test
    public void updateTest() {
        Long userId = 1L;
        UserDto userDto = new UserDto("Updated", "update.svir@mail.com");
        User user = new User("Anastasia", "an.svir@mail.com");
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.updateUser(userId, userDto));
    }

    public void deleteTest() {
        Long userId = 1L;
        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

}
