package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.model.dto.BookingDto;

import java.util.List;

public interface BookingServiceDao {
    BookingDto createBooking(BookingDto.BookingDtoReq booking, Integer userId);

    BookingDto approveOrReject(Integer userId, Integer bookingId, String solution);

    BookingDto getBooking(Integer bookingId, Integer userId);

    List<BookingDto> getBookingsByUserId(Integer userId, String state, boolean isOwner);
}
