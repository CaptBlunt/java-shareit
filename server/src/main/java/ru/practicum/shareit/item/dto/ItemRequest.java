package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequest {

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;

    public ItemRequest(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
