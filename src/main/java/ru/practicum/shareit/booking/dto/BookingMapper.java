package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingMapper {

    public Booking bookingForCreate(Booking newBooking, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(newBooking.getStart());
        booking.setEnd(newBooking.getEnd());
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        return booking;
    }

    public Booking bookingFromBookingRequest(BookingRequest request, Integer userId) {
        Booking booking = new Booking();
        Item item = new Item();
        item.setId(request.getItemId());
        booking.setItem(item);
        User booker = new User();
        booker.setId(userId);
        booking.setBooker(booker);
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());

        return booking;
    }

    public BookingResponse bookingForResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setStart(booking.getStart());
        response.setEnd(booking.getEnd());
        response.setStatus(booking.getStatus());
        UserResponse.BookerDto booker = new UserResponse.BookerDto();
        booker.setId(booking.getBooker().getId());
        response.setBooker(booker);

        ItemForBooking itemForBooking = new ItemForBooking();
        itemForBooking.setId(booking.getItem().getId());
        itemForBooking.setName(booking.getItem().getName());
        response.setItem(itemForBooking);

        return response;
    }

    public BookingResponse bookingResponseFromBooking(Booking booking, Item item) {
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        UserResponse.BookerDto booker = new UserResponse.BookerDto();
        booker.setId(booking.getBooker().getId());
        dto.setBooker(booker);

        ItemForBooking itemD = new ItemForBooking();
        itemD.setId(item.getId());
        itemD.setName(item.getName());
        dto.setItem(itemD);

        return dto;
    }

    public List<BookingResponse> listBookingResponseFromBookings(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> bookingResponseFromBooking(booking, booking.getItem()))
                .collect(Collectors.toList());
    }
}
