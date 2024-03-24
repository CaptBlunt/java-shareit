package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateItemRequest {
    @NotBlank(message = "Поле описания не может быть пустым")
    @NotNull(message = "Поле описания не может быть null")
    private String description;
}
