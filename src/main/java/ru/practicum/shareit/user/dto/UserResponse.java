package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserResponse {

    private Integer id;

    private String email;

    private String name;

    @Data
    public static class BookerDto {
        private Integer id;
    }
}

