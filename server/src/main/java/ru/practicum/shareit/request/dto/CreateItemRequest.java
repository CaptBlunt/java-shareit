package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;



@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateItemRequest {
    private String description;
}
