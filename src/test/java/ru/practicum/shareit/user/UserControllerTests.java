package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void findAllUser() throws Exception {
        List<User> users = List.of(new User());

        when(userService.findAll()).thenReturn(users);

        String result = mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(users))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService, Mockito.times(1)).findAll();
        assertEquals(result, mapper.writeValueAsString(users));
    }

    @Test
    void findUser() throws Exception {
        User user = new User(
                "AnastasiaUpdate",
                "update.svir@mail.com");
        Long userId = 1L;
        user.setId(userId);

        when(userService.findById(userId)).thenReturn(user);

        mvc.perform(get("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, Mockito.times(1)).findById(userId);
    }

    @Test
    void saveNewUserValid() throws Exception {
        UserDto userDto = new UserDto(
                "Anastasia",
                "anastasia.svir@mail.com");
        User user = new User(
                "Anastasia",
                "anastasia.svir@mail.com");
        Long userId = 1L;
        user.setId(userId);

        when(userService.createUser(userDto)).thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, Mockito.times(1)).createUser(userDto);
    }

    @Test
    void saveNewUserNotValid() throws Exception {
        UserDto userDto = new UserDto(
                "name",
                "mail.com");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(userDto);
    }

    @Test
    void updateUserValid() throws Exception {
        UserDto userDto = new UserDto(
                "AnastasiaUpdate",
                "update.svir@mail.com");
        User user = new User(
                "AnastasiaUpdate",
                "update.svir@mail.com");
        Long userId = 1L;
        user.setId(userId);

        when(userService.updateUser(userId, userDto)).thenReturn(user);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        verify(userService, Mockito.times(1)).updateUser(userId, userDto);
    }

    @Test
    void updateUserNotValid() throws Exception {
        UserDto userDto = new UserDto(
                "AnastasiaUpdate",
                "update.svir.com");
        User user = new User(
                "AnastasiaUpdate",
                "update.svir.com");
        Long userId = 1L;
        user.setId(userId);

        when(userService.updateUser(userId, userDto)).thenReturn(user);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(userId, userDto);
    }

    @Test
    void delete() throws Exception {
        Long userId = 1L;

        //Почему-то не получилось импортировать, чтобы было просто delete
        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1)).deleteById(userId);
    }
}
