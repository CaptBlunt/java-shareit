package ru.practicum.shareit.user.dto;

import lombok.Data;

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

