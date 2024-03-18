package ru.practicum.shareit.comments.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class CommentResponse {

    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}
