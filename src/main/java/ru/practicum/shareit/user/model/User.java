package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    @NotNull
    @NotBlank
    private Long id;
    private String name;
    @Email
    @NotNull
    @NotBlank
    private String email;
}
