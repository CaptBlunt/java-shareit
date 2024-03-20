package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestResponseForCreate {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private LocalDateTime currentDate;
}
