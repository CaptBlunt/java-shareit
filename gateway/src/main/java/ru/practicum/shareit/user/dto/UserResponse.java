package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

@Data
public class UserResponse {

    @Min(1)
    private Integer id;

    @Email(message = "Некорректный email")
    private String email;

    private String name;

    @Data
    public static class BookerDto {
        private Integer id;
    }
}