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
import ru.practicum.shareit.booking.dto.BookingRequest;
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

    Item item = Item.builder()
            .id(1)
            .build();
    Booking booking = Booking.builder()
            .id(1)
            .status(BookingStatus.APPROVED)
            .booker(User.builder()
                    .id(1)
                    .build())
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(item)
            .build();

    Booking bookingForGet = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(Item.builder()
                    .id(1)
                    .owner(User.builder()
                            .id(1)
                            .build())
                    .available(true)
                    .build())
            .booker(User.builder()
                    .id(2)
                    .build())
            .status(BookingStatus.WAITING)
            .build();

    List<Booking> bookings = List.of(booking, bookingForGet);

    BookingRequest bookingRequestForCreate = BookingRequest.builder()
            .itemId(1)
            .build();

    BookingRequest bookingRequestNotValidDate = BookingRequest.builder()
            .itemId(1)
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().minusDays(3))
            .build();

    Booking bookingReqNotValidDate = Booking.builder()
            .start(LocalDateTime.now().minusDays(1))
            .end(LocalDateTime.now().minusDays(3))
            .build();

    @Test
    void getBookingByIdForOwnerOrBooker() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        when(bookingService.getBooking(bookingId, userId)).thenReturn(bookingForGet);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(bookingForGet.getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingForGet.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingForGet.getItem().getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingForGet.getStatus()))));
    }

    @Test
    void approvedBookingWhenTrue() throws Exception {
        int userId = 2;
        int bookingId = 1;
        String approve = "true";

        when(bookingService.approveOrReject(bookingId, userId, "true")).thenReturn(bookingForGet);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("approved", approve))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(bookingForGet.getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingForGet.getStatus()))))
                .andExpect(jsonPath("$.booker.id", is(bookingForGet.getBooker().getId())));
    }

    @Test
    void createBookingWhenBookingValid() throws Exception {
        Integer userId = 2;

        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings", bookingRequestForCreate)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(bookingRequestForCreate))
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

        mockMvc.perform(post("/bookings", bookingRequestNotValidDate)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(bookingRequestNotValidDate))
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
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(booking.getId())))
                .andExpect(jsonPath("$[0].item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$[0].booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$[1].id", is(bookingForGet.getId())))
                .andExpect(jsonPath("$[1].item.id", is(bookingForGet.getItem().getId())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingForGet.getBooker().getId())));
    }
}