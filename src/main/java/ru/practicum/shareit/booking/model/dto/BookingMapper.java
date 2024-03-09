package ru.practicum.shareit.booking.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemRepository itemRepository;

    public Booking bookingFromDto(BookingDto.BookingDtoReq dto) {
        Booking booking = new Booking();
        booking.setItem(booking.getItem());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public BookingDto dtoFromBooking(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        UserDto.BookerDto booker = new UserDto.BookerDto();
        booker.setId(booking.getBooker());
        dto.setBooker(booker);

        ItemDto.ItemDtoForBooking itemD = new ItemDto.ItemDtoForBooking();
        Item item = itemRepository.getReferenceById(booking.getItem());
        itemD.setId(item.getId());
        itemD.setName(item.getName());
        dto.setItem(itemD);

        return dto;
    }

    public List<BookingDto> listDtoFromBookings(List<Booking> bookings) {
        return bookings.stream()
                .map(this::dtoFromBooking)
                .collect(Collectors.toList());
    }
}
