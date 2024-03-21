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
import java.util.Arrays;
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

    Item itemTwo = Item.builder()
            .id(2)
            .name("Test")
            .description("Test")
            .owner(booker)
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

    Booking bookingPast = Booking.builder()
            .id(5)
            .start(LocalDateTime.now().minusDays(4))
            .end(LocalDateTime.now().minusDays(3))
            .status(BookingStatus.PAST)
            .booker(booker)
            .item(item)
            .build();

    Booking bookingFuture = Booking.builder()
            .id(5)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .status(BookingStatus.FUTURE)
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

    Booking bookingApproved = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.APPROVED)
            .booker(booker)
            .item(item)
            .build();

    Booking newBooking = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .status(BookingStatus.APPROVED)
            .booker(booker)
            .item(item)
            .build();


    @Test
    void paginationNotValid() {
        ValidateException exception = assertThrows(ValidateException.class, () -> bookingService.getBookingsByUserId(1, "ALL", true, 0, 0));
        assertEquals("Проверьте указанные параметры", exception.getMessage());
    }

    @Test
    void approveBooking() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingSaved);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(item);

        when(bookingRepository.save(bookingSaved)).thenReturn(newBooking);

        Booking result = bookingService.approveOrReject(1, 2, "true");

        assertEquals(result, newBooking);
    }

    @Test
    void approveBookingWhenUserNotOwner() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingApproved);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(itemTwo);

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.approveOrReject(1, 1, "true"));
        assertEquals("Статус нельзя изменить", exception.getMessage());
    }

    @Test
    void approveBookingWhenStatusNotWaiting() {
        when(bookingRepository.getReferenceById(anyInt())).thenReturn(bookingSaved);

        when(itemRepository.getReferenceById(anyInt())).thenReturn(itemTwo);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.approveOrReject(1, 2, "true"));
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void getBookingsWhenUserNotOwner() {
        List<Booking> bookings = Arrays.asList(bookingTwo, bookingOne);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerAndStatus(anyInt(), eq("WAITING"), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(10, "WAITING", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
        assertEquals(bookings.get(1), result.get(1));
    }

    @Test
    void getBookingsWhenUserOwnerStatusPast() {
        List<Booking> bookings = Collections.singletonList(bookingPast);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdPastBookings(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "PAST", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserOwnerStatusWaiting() {
        List<Booking> bookings = Collections.singletonList(bookingTwo);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdAndStatus(anyInt(), eq("WAITING"), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "WAITING", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserOwnerStatusFuture() {
        List<Booking> bookings = Collections.singletonList(bookingFuture);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByOwnerIdFutureBookings(anyInt(), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(2, "FUTURE", true, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusFuture() {
        List<Booking> bookings = Collections.singletonList(bookingFuture);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerIdFutureBookings(eq(1), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "FUTURE", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusPast() {
        List<Booking> bookings = Collections.singletonList(bookingPast);

        PageRequest pageable = PageRequest.of(1 / 10, 10);

        when(bookingRepository.findByBookerIdPastBookings(eq(1), eq(pageable))).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUserId(1, "PAST", false, 1, 10);

        assertEquals(bookings.size(), result.size());
        assertEquals(bookings.get(0), result.get(0));
    }

    @Test
    void getBookingsWhenUserNotOwnerStatusAll() {
        List<Booking> bookings = Arrays.asList(bookingPast, bookingFuture);

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