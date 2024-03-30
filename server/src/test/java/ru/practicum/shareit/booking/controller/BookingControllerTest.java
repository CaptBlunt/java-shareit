package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingServiceImpl bookingService;

    Item item = new Item();

    User user = new User();

    Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED);

    List<Booking> bookings = List.of(booking);

    Booking bookingReqNotValidDate = new Booking(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(3));

    @Test
    void getBookingByIdForOwnerOrBooker() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        when(bookingService.getBooking(bookingId, userId)).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void approvedBookingWhenTrue() throws Exception {
        int userId = 2;
        int bookingId = 1;
        String approve = "true";

        when(bookingService.approveOrReject(bookingId, userId, true)).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("approved", approve))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())));
    }

    @Test
    void createBookingWhenBookingValid() throws Exception {
        Integer userId = 2;

        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings", booking)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void createBookingWhenNotValidDate() throws Exception {
        Integer userId = 2;

        when(bookingService.createBooking(bookingReqNotValidDate)).thenThrow(new AccessibilityErrorException("Не верные даты"));

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.createBooking(bookingReqNotValidDate));

        mockMvc.perform(post("/bookings", bookingReqNotValidDate)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(bookingReqNotValidDate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error", is(exception.getMessage())));
    }

    @Test
    void getListBookingsWhenUserBookerAndStatusAll() throws Exception {
        Integer userId = 2;
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        when(bookingService.getBookingsByUserId(userId, state, false, from, size)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId())))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.[0].status", is(String.valueOf(booking.getStatus()))));
    }
}