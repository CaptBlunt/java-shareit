package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemUpdateRequest {

    private Integer id;

    @NotBlank(message = "Поле названия не может быть пустым")
    @NotNull(message = "Поле названия не может быть null")
    private String name;

    @NotBlank(message = "Поле описания не может быть пустым")
    @NotNull(message = "Поле описания не может быть null")
    private String description;

    @NotBlank(message = "Поле доступности не может быть пустым")
    @NotNull(message = "Поле доступности не может быть null")
    private Boolean available;
}
