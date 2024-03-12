package ru.practicum.shareit.comments.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;

}
