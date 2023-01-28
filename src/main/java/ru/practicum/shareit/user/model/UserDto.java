package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
