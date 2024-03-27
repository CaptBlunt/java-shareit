package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.comments.dto.CommentResponse;

import java.util.List;

@Data
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemResponse {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private ItemForOwner lastBooking;

    private ItemForOwner nextBooking;

    private List<CommentResponse> comments;

    private Integer requestId;

    public ItemResponse(Integer id, String name, String description, boolean available, List<CommentResponse> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = comments;
    }

    public ItemResponse(int id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Data
    @Builder
    @EqualsAndHashCode(of = "id")
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class ItemForOwner {
        private Integer id;
        private Integer bookerId;
    }

}
