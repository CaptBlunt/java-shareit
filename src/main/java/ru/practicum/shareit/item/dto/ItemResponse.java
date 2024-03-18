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
