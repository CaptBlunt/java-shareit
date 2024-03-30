package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {
    @NotBlank(message = "Поле имени не может быть пустым")
    public String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Поле не может быть null")
    public String email;
}
