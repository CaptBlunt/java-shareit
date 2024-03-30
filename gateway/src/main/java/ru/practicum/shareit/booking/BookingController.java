package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;

    @RestController
    @RequestMapping(path = "/bookings")
    @RequiredArgsConstructor
    public class BookingController {
        private final BookingClient bookingClient;

        @PostMapping
        public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingRequest booking,
                                            @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
            return bookingClient.createBooking(booking, userId);
        }

        @PatchMapping("/{bookingId}")
        public ResponseEntity<Object> approvedBooking(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                      @PathVariable Integer bookingId,
                                                      @RequestParam Boolean approved) {
            return bookingClient.approvedBooking(userId, bookingId, approved);
        }

        @GetMapping(path = {"/{bookingId}"})
        public ResponseEntity<Object> getBookingById(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                     @PathVariable(required = false, value = "bookingId") @Min(1) Integer bookingId) {
            return bookingClient.getBookingById(bookingId, userId);
        }

        @GetMapping
        public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL") String state,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
            return bookingClient.getBookingsByUserId(userId, state, from, size);
        }

        @GetMapping("/owner")
        public ResponseEntity<Object> getBookingsForItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(required = false, defaultValue = "ALL") String state,
                                                                 @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
            return bookingClient.getBookingsByOwner(userId, state, from, size);
        }
}
