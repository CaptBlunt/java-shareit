package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class RequestForUser {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private LocalDateTime currentDate;
    private List<ItemForRequest> items;
}
