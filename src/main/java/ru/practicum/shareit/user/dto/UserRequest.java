package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRequest {
    @NotBlank(message = "Поле имени не может быть пустым")
    @NotNull(message = "Поле имени не может быть null")
    public String name;

    @Email(message = "Некорректный email")
    @NotNull(message = "Поле не может быть null")
    public String email;
}
