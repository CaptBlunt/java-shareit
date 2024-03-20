package ru.practicum.shareit.comments.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class CommentResponse {

    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}
