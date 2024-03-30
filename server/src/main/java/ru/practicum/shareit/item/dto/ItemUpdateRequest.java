package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateRequest {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;
}
