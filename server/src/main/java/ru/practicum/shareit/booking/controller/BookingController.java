package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest booking,
                                         @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /bookings от пользователя id {} с телом {}", userId, booking);
        BookingResponse response = bookingMapper.bookingForResponse(bookingService.createBooking(bookingMapper.bookingFromBookingRequest(booking, userId)));
        log.info("Отправлен ответ createBooking /bookings с телом {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approvedBooking(@PathVariable Integer bookingId,
                                           @RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                           @RequestParam Boolean approved) {
        log.info("Пришёл PATCH запрос /bookings/{} от пользователя id {}", bookingId, userId);
        BookingResponse response = bookingMapper.bookingForResponse(bookingService.approveOrReject(bookingId, userId, approved));
        log.info("Отправлен ответ approvedBooking /bookings/{} с телом {}", bookingId, response);
        return response;
    }

    @GetMapping(path = {"/", "/{bookingId}"})
    public BookingResponse getBookingById(@PathVariable(required = false, value = "bookingId") Integer bookingId,
                                          @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /bookings/{} от пользователя id {}", bookingId, userId);
        BookingResponse response = bookingMapper.bookingForResponse(bookingService.getBooking(bookingId, userId));
        log.info("Отправлен ответ getBookingById /bookings/{} с телом {}", bookingId, response);
        return response;
    }

    @GetMapping
    public List<BookingResponse> getBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state,
                                                     @RequestParam(required = false) @Validated Integer from, @RequestParam(required = false) @Validated Integer size) {
        log.info("Пришёл GET запрос /bookings от пользователя id {}", userId);
        List<BookingResponse> response = bookingMapper.listBookingResponseFromBookings(bookingService.getBookingsByUserId(userId, state, false, from, size));
        log.info("Отправлен ответ getBookingsByUserId /bookings с телом {}", response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsForItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                                             @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Пришёл GET запрос /bookings/owner от пользователя id {}", userId);
        List<BookingResponse> response = bookingMapper.listBookingResponseFromBookings(bookingService.getBookingsByUserId(userId, state, true, from, size));
        log.info("Отправлен ответ getBookingsForItemsByUserId /bookings/owner с телом {}", response);
        return response;
    }
}
