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
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
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

    User booker = new User(1, "bob@gmail.com", "Bob");

    User owner = new User(2, "bob2@gmail.com", "Bob2");

    Item item = new Item(1, "Test", "Test", owner, true);

    Item itemTwo = new Item(2, "Test", "Test", booker, true);

    Booking bookingOne = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), BookingStatus.WAITING, booker, item);

    Booking bookingForSave = new Booking(item, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), booker);

    Booking bookingApproved = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), BookingStatus.APPROVED, booker, item);


    @Test
    void paginationNotValid() {
        ValidateException exception = assertThrows(ValidateException.class, () -> bookingService.getBookingsByUserId(1, "ALL", true, 0, 0));
        assertEquals("Проверьте указанные параметры", exception.getMessage());
    }

    @Test
    void approveBooking() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingOne);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(item);

        when(bookingRepository.save(bookingOne)).thenReturn(bookingOne);

        Booking result = bookingService.approveOrReject(1, 2, true);

        assertEquals(result, bookingOne);
    }

    @Test
    void approveBookingWhenStatusNotWaiting() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingApproved);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(itemTwo);

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.approveOrReject(1, 1, true));
        assertEquals("Статус нельзя изменить", exception.getMessage());
    }

    @Test
    void approveBookingWhenUserNotOwner() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingOne);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(itemTwo);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.approveOrReject(1, 2, true));
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void getBookingsWhenUserNotOwner() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerAndStatus(anyInt(), eq("WAITING"), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(10, "WAITING", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserOwnerStatusPast() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdPastBookings(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "PAST", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserOwnerStatusWaiting() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdAndStatus(anyInt(), eq("WAITING"), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "WAITING", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserOwnerStatusFuture() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdFutureBookings(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "FUTURE", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusFuture() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerIdFutureBookings(eq(1), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "FUTURE", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusPast() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerIdPastBookings(eq(1), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "PAST", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusAll() {
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerOrderByStartDesc(eq(1), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "ALL", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenStateUnknown() {
        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.getBookingsByUserId(1, "UNKNOWN", true, 1, 10));

        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getBookingsWhenBookingsIsEmpty() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBookingsByUserId(1, "ALL", true, 1, 10));

        assertEquals("Бронирований не найдено", exception.getMessage());
    }

    @Test
    void getBookingWhenUserOwner() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingOne));

        Booking result = bookingService.getBooking(bookingOne.getId(), 2);

        assertEquals(result, bookingOne);
    }

    @Test
    void getBookingWhenBookingNotFound() {
        when(bookingRepository.findById(anyInt())).thenThrow(new NotFoundException("Бронирование не найдено"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBooking(10000, 10000));

        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getBookingWhenUserNotOwner() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingOne));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 10));

        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void createBookingWhenUserExistsItemExistsAndBookingDatesValid() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingMapper.bookingForCreate(any(), any(), any())).thenReturn(bookingForSave);
        when(bookingRepository.save(bookingForSave)).thenReturn(bookingOne);

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
        List<Booking> bookings = Collections.singletonList(bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerId(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "ALL", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }
}