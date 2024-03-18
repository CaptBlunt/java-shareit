package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = "itemId")
public class BookingRequest {

    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
