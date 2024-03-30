package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    public PageRequest pagination(Integer from, Integer size) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = bookingRepository.findAll().size();
        }

        if ((from < 0 || size < 0) || (size == 0)) {
            throw new ValidateException("Проверьте указанные параметры");
        }
        return PageRequest.of(from / size, size);
    }

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

        if (newBooking.getBooker().getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        return bookingRepository.save(bookingMapper.bookingForCreate(newBooking, user, item));
    }

    @Override
    public Booking approveOrReject(Integer bookingId, Integer userId, Boolean solution) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (!booking.getStatus().toString().equals("WAITING")) {
            throw new AccessibilityErrorException("Статус нельзя изменить");
        }
        if (solution) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId, String state, boolean isOwner, Integer from, Integer size) {
        List<Booking> bookings = new ArrayList<>();
        BookingStatus status = BookingStatus.from(state);
        if (status == null) {
            throw new AccessibilityErrorException("Unknown state: " + state);
        } else {
            PageRequest pageable = pagination(from, size);

            if (isOwner) {
                switch (status) {
                    case ALL:
                        bookings = bookingRepository.findByOwnerId(userId, pageable);
                        break;
                    case PAST:
                        bookings = bookingRepository.findByOwnerIdPastBookings(userId, pageable);
                        break;
                    case WAITING:
                    case REJECTED:
                        bookings = bookingRepository.findByOwnerIdAndStatus(userId, state, pageable);
                        break;
                    case FUTURE:
                        bookings = bookingRepository.findByOwnerIdFutureBookings(userId, pageable);
                        break;
                    case CURRENT:
                        bookings = bookingRepository.findByOwnerIdCurrentBookings(userId, pageable);
                }
            } else {
                switch (status) {
                    case ALL:
                        bookings = bookingRepository.findByBookerOrderByStartDesc(userId, pageable);
                        break;
                    case PAST:
                        bookings = bookingRepository.findByBookerIdPastBookings(userId, pageable);
                        break;
                    case WAITING:
                    case REJECTED:
                        bookings = bookingRepository.findByBookerAndStatus(userId, state, pageable);
                        break;
                    case FUTURE:
                        bookings = bookingRepository.findByBookerIdFutureBookings(userId, pageable);
                        break;
                    case CURRENT:
                        bookings = bookingRepository.findByBookerIdCurrentBookings(userId, pageable);
                }
            }
        }
        if (bookings.isEmpty()) {
            throw new NotFoundException("Бронирований не найдено");
        }
        return bookings;
    }
}
