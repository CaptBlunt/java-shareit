package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    @Override
    public Booking createBooking(Booking newBooking) {
        User user = userRepository.findById(newBooking.getBooker().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(newBooking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getAvailable().equals(false)) {
            throw new AccessibilityErrorException("Вещь не доступна");
        }
        LocalDateTime start = newBooking.getStart();
        LocalDateTime end = newBooking.getEnd();

        if (start == null || end == null || end.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.equals(end) || start.isBefore(LocalDateTime.now())) {
            throw new AccessibilityErrorException("Не верные даты");
        }

        if (newBooking.getBooker().getId().equals(item.getOwnerId())) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }
        Booking bookingSave = bookingRepository.save(bookingMapper.bookingForCreate(newBooking, user, item));

        return bookingRepository.getReferenceById(bookingSave.getId());
    }

    @Override
    public Booking approveOrReject(Integer bookingId, Integer userId, String solution) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (!item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (!booking.getStatus().toString().equals("WAITING")) {
            throw new AccessibilityErrorException("Статус нельзя изменить");
        }
        if (solution.equals("true")) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking bookingUpd = bookingRepository.save(booking);

        return bookingRepository.getReferenceById(bookingUpd.getId());
    }

    @Override
    public Booking getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId, String state, boolean isOwner) {
        List<Booking> bookings = new ArrayList<>();
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new AccessibilityErrorException("Unknown state: " + state);
        } else {
            if (isOwner) {
                switch (status) {
                    case ALL:
                        bookings = bookingRepository.findByOwnerId(userId);
                        break;
                    case PAST:
                        bookings = bookingRepository.findByOwnerIdPastBookings(userId);
                        break;
                    case WAITING:
                    case REJECTED:
                        bookings = bookingRepository.findByOwnerIdAndStatus(userId, state);
                        break;
                    case FUTURE:
                        bookings = bookingRepository.findByOwnerIdFutureBookings(userId);
                        break;
                    case CURRENT:
                        bookings = bookingRepository.findByOwnerIdCurrentBookings(userId);
                }
            } else {
                switch (status) {
                    case ALL:
                        bookings = bookingRepository.findByBookerOrderByStartDesc(userId);
                        break;
                    case PAST:
                        bookings = bookingRepository.findByBookerIdPastBookings(userId);
                        break;
                    case WAITING:
                    case REJECTED:
                        bookings = bookingRepository.findByBookerAndStatus(userId, state);
                        break;
                    case FUTURE:
                        bookings = bookingRepository.findByBookerIdFutureBookings(userId);
                        break;
                    case CURRENT:
                        bookings = bookingRepository.findByBookerIdCurrentBookings(userId);
                }
            }
        }
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено");
        }
        return bookings;
    }
}
