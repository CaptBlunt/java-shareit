package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comments.dto.CommentResponse;

import java.util.List;

@Data
public class ItemResponse {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private ItemForOwner lastBooking;

    private ItemForOwner nextBooking;

    private List<CommentResponse> comments;

    @Data
    public static class ItemForOwner {
        private Integer id;
        private Integer bookerId;
    }
}
