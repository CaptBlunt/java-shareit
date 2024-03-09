package ru.practicum.shareit.booking.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;


@Data
public class BookingDto {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserDto.BookerDto booker;
    private ItemDto.ItemDtoForBooking item;

    @Data
    public static class BookingDtoReq {
        private Integer id;
        private Integer itemId;
        private LocalDateTime start;
        private LocalDateTime end;
    }
}
