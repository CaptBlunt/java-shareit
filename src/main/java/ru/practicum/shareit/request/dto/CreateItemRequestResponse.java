package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateItemRequestResponse {

    private Integer id;

    private String description;

    private LocalDateTime created;

    private LocalDateTime currentDate;

    public CreateItemRequestResponse(int id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }
}
