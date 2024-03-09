package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto.BookingDtoReq booking,
                                    @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /bookings от пользователя id {} с телом {}", userId, booking);
        BookingDto response = bookingService.createBooking(booking, userId);
        log.info("Отправлен ответ createBooking /bookings с телом {}", response);
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@PathVariable Integer bookingId,
                                      @RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                      @RequestParam String approved) {
        log.info("Пришёл PATCH запрос /bookings/{} от пользователя id {}", bookingId, userId);
        BookingDto response = bookingService.approveOrReject(bookingId, userId, approved);
        log.info("Отправлен ответ approvedBooking /bookings/{} с телом {}", bookingId, response);
        return response;
    }

    @GetMapping(path = {"/", "/{bookingId}"})
    public BookingDto getBookingById(@PathVariable(required = false, value = "bookingId") Integer bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /bookings/{} от пользователя id {}", bookingId, userId);
        BookingDto response = bookingService.getBooking(bookingId, userId);
        log.info("Отправлен ответ getBookingById /bookings/{} с телом {}", bookingId, response);
        return response;
    }

    @GetMapping
    public List<BookingDto> getBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Пришёл GET запрос /bookings от пользователя id {}", userId);
        //List<BookingDto> response = bookingDao.findByBookerAndStatusOrderByStartDesc(userId, state);
        List<BookingDto> response = bookingService.getBookingsByUserId(userId, state, false);
        log.info("Отправлен ответ getBookingsByUserId /bookings с телом {}", response);
        return response;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Пришёл GET запрос /bookings/owner от пользователя id {}", userId);
        //List<BookingDto> response = bookingDao.getBookingsByUserId(userId, state);
        List<BookingDto> response = bookingService.getBookingsByUserId(userId, state, true);
        log.info("Отправлен ответ getBookingsForItemsByUserId /bookings/owner с телом {}", response);
        return response;
    }
}
