package ru.practicum.shareit.comments.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Integer id;

    private String text;

    private String authorName;

    private LocalDateTime created;

    @Data
    public static class CommentDtoPost {
        private String text;
    }
}
