package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dao.BookingServiceDao;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingServiceDao {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingDto.BookingDtoReq bookingDtoReq, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDtoReq.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getAvailable().equals(false)) {
            throw new AccessibilityErrorException("Вещь не доступна");
        }
        LocalDateTime start = bookingDtoReq.getStart();
        LocalDateTime end = bookingDtoReq.getEnd();

        if (start == null || end == null || end.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.equals(end) || start.isBefore(LocalDateTime.now())) {
            throw new AccessibilityErrorException("Не верные даты");
        }

        if (userId.equals(item.getOwnerId())) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }
        Booking booking1 = bookingMapper.bookingFromDto(bookingDtoReq);

        booking1.setStatus(BookingStatus.WAITING);
        booking1.setBooker(userId);
        booking1.setItem(bookingDtoReq.getItemId());
        bookingRepository.save(booking1);

        return bookingMapper.dtoFromBooking(bookingRepository.getReferenceById(booking1.getId()));
    }

    @Override
    public BookingDto approveOrReject(Integer bookingId, Integer userId, String solution) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Item item = itemRepository.getReferenceById(booking.getItem());
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
        bookingRepository.save(booking);

        return bookingMapper.dtoFromBooking(bookingRepository.getReferenceById(booking.getId()));
    }

    @Override
    public BookingDto getBooking(Integer bookingId, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        Item item = itemRepository.findById(booking.getItem())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!booking.getBooker().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        return bookingMapper.dtoFromBooking(bookingRepository.getReferenceById(booking.getId()));
    }

    @Override
    public List<BookingDto> getBookingsByUserId(Integer userId, String state, boolean isOwner) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
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
        return bookingMapper.listDtoFromBookings(bookings);
    }
}
