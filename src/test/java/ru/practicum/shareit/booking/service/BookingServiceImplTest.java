package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    User booker = User.builder()
            .id(1)
            .email("bob@gmail.com")
            .name("Bob")
            .build();

    User owner = User.builder()
            .id(2)
            .email("bob2@gmail.com")
            .name("Bob2")
            .build();

    Item item = Item.builder()
            .id(1)
            .name("Test")
            .description("Test")
            .owner(owner)
            .available(true)
            .build();

    Booking bookingOne = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.WAITING)
            .booker(booker)
            .item(item)
            .build();

    Booking bookingTwo = Booking.builder()
            .id(2)
            .start(LocalDateTime.now().plusDays(4))
            .end(LocalDateTime.now().plusDays(5))
            .status(BookingStatus.WAITING)
            .booker(booker)
            .item(item)
            .build();

    Booking bookingForSave = Booking.builder()
            .item(item)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .booker(booker)
            .build();
    Booking bookingSaved = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.WAITING)
            .booker(booker)
            .item(item)
            .build();

    @Test
    void createBookingWhenUserExistsItemExistsAndBookingDatesValid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingMapper.bookingForCreate(any(), any(), any())).thenReturn(bookingForSave);
        when(bookingRepository.save(bookingForSave)).thenReturn(bookingSaved);

        Booking savedBooking = bookingService.createBooking(bookingForSave);

        assertNotNull(savedBooking);
        assertEquals(1, savedBooking.getId());
        assertEquals(BookingStatus.WAITING, savedBooking.getStatus());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
        assertEquals(item.getId(), savedBooking.getItem().getId());

        verify(bookingRepository).save(any());
    }

    @Test
    void createBookingWhenUserExistsItemDoesNotExists() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenThrow(new NotFoundException("Вещь не найдена"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingForSave));

        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void createBookingWhenUserExistsItemExistsBookingDatesNotValid() {
        bookingForSave.setStart(LocalDateTime.now().minusDays(1));
        bookingForSave.setEnd(LocalDateTime.now().plusDays(3));

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.createBooking(bookingForSave));

        assertEquals("Не верные даты", exception.getMessage());
    }

    @Test
    void getBookingsWhenUserOwnerSearchStatusAllPaginationValid() {
        List<Booking> bookings = Arrays.asList(bookingTwo, bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerId(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "ALL", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
        assertEquals(bookings.get(1), result.get(1));
    }
}