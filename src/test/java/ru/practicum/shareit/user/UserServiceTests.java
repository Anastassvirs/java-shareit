package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.SameFieldException;
import ru.practicum.shareit.exceptions.SaveUserException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private Long userId;
    private User user;
    private UserDto userDto;
    private List<User> users;

    @BeforeEach
    void init() {
        userId = 1L;
        user = new User("AnastasiaUpdate", "update.svir@mail.com");
        user.setId(userId);
        userDto = new UserDto("Anastasia", "anastasia.svir@mail.com");
        users = List.of(user);
    }

    @Test
    public void findAllTest() {
        when(userRepository.findAll()).thenReturn(users);
        assertEquals(users, userService.findAll());
    }

    @Test
    public void findByIdTest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.findById(userId));
    }

    @Test
    public void saveTest() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        assertEquals(user, userService.createUser(userDto));
    }

    @Test
    public void saveNullNameTest() {
        UserDto userDto = new UserDto(null, "an.svir@mail.com");
        Throwable thrown = catchThrowable(() -> {
            userService.createUser(userDto);
        });
        assertThat(thrown).isInstanceOf(WrongParametersException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Неправильно заполнены поля создаваемого пользователя", thrown.getMessage());
    }

    @Test
    public void saveAlreadyExistEmailTest() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));
        UserDto userDto = new UserDto("Anastasia", "an.svir@mail.com");
        Throwable thrown = catchThrowable(() -> {
            userService.createUser(userDto);
        });
        assertThat(thrown).isInstanceOf(SaveUserException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Неправильно заполнены поля создаваемого пользователя", thrown.getMessage());
    }

    @Test
    public void updateTest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.updateUser(userId, userDto));
    }

    @Test
    public void updateErrorsTest() {
        userDto = new UserDto("Anastasia", "update.svir@mail.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));

        Throwable thrown = catchThrowable(() -> {
            userService.updateUser(userId, userDto);
        });
        assertThat(thrown).isInstanceOf(SameFieldException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Данный email уже зарегистрирован", thrown.getMessage());

        when(userRepository.findAll()).thenReturn(List.of());
        thrown = catchThrowable(() -> {
            userService.updateUser(userId, userDto);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Такого пользователя не существует", thrown.getMessage());

    }

    @Test
    public void deleteTest() {
        userService.deleteById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    public void emailExistCheckTest() {
        String email = "update.svir@mail.com";

        when(userRepository.findAll()).thenReturn(List.of(user));
        assertEquals(true, userService.emailAlreadyExist(email));

        when(userRepository.findAll()).thenReturn(List.of());
        assertEquals(false, userService.emailAlreadyExist(email));
    }

    @Test
    public void userExistCheckTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        assertEquals(true, userService.userExistById(userId));

        when(userRepository.findAll()).thenReturn(List.of());
        assertEquals(false, userService.userExistById(userId));
    }
}
