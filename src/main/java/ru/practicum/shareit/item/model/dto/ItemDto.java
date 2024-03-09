package ru.practicum.shareit.item.model.dto;

import lombok.Data;
import ru.practicum.shareit.comments.model.dto.CommentDto;

import java.util.List;

@Data
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private ItemDtoForOwner lastBooking;

    private ItemDtoForOwner nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class ItemDtoForBooking {
        private Integer id;
        private String name;
    }

    @Data
    public static class ItemDtoForOwner {
        private Integer id;
        private Integer bookerId;
    }
}
