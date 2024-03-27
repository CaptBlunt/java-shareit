package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemForRequest {

    private Integer id;
    @NotBlank(message = "Поле названия не может быть пустым")
    @NotNull(message = "Поле названия не может быть null")
    private String name;

    @NotBlank(message = "Поле описания не может быть пустым")
    @NotNull(message = "Поле описания не может быть null")
    private String description;

    @NotNull(message = "Поле доступности не может быть null")
    private Boolean available;

    private Integer requestId;
}
