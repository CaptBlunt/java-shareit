package ru.practicum.shareit.comments.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentRequest {
    @NotNull
    private String text;
}
