package ru.practicum.shareit.user.model;

public class UserMapper {
    private static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }
}
