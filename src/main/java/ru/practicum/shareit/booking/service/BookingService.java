package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking approveOrReject(Integer userId, Integer bookingId, String solution);

    Booking getBooking(Integer bookingId, Integer userId);

    List<Booking> getBookingsByUserId(Integer userId, String state, boolean isOwner);
}
