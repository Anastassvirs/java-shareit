package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class UserMapper {
    public static UserDto toUserDto(@NotNull User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(@NotNull UserDto userDto) {
        return new User(
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
