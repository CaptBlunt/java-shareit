package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemForRequest {

    private Integer id;
    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;
}
