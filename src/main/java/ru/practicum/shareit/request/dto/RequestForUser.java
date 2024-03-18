package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestForUser {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private LocalDateTime currentDate;
    private List<ItemForRequest> items;
}
