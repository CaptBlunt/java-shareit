package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(of = "id")
public class BookingResponse {

    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private UserResponse.BookerDto booker;
    private ItemForBooking item;
}
