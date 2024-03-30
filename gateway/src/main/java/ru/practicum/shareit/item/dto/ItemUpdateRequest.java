package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ItemUpdateRequest {

    private Integer id;

    @NotBlank(message = "Поле названия не может быть пустым")
    private String name;

    @NotBlank(message = "Поле описания не может быть пустым")
    private String description;

    private Boolean available;
}
