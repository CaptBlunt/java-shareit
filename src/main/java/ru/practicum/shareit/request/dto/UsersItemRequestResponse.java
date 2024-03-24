package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class UsersItemRequestResponse {
    private Integer id;

    private String description;

    private LocalDateTime created;

    private LocalDateTime currentDate;

    private List<ItemForRequest> items;

    public UsersItemRequestResponse(int id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }

    public UsersItemRequestResponse(int id, String description, LocalDateTime created, LocalDateTime currentDate) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.currentDate = currentDate;
    }
}
