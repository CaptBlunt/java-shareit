package ru.practicum.shareit.user.model.dto;

import lombok.Data;

@Data
public class UserDto {

    private Integer id;

    private String email;

    private String name;

    @Data
    public static class BookerDto {
        private Integer id;
    }
}

