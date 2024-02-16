package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {

    private Integer id;

    @Email(message = "Некорректный email")
    @NotNull(message = "Поле не может быть null")
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым")
    @NotNull(message = "Поле имени не может быть null")
    private String name;
}