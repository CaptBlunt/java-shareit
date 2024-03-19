package ru.practicum.shareit.comments.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentRequest {
    private String text;
}
