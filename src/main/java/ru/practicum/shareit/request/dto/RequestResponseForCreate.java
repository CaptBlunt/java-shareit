package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponseForCreate {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private LocalDateTime currentDate;
}
