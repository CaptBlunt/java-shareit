package ru.practicum.shareit.item;

import lombok.Data;

@Data
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;
}
