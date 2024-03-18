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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void getBookingByIdForOwnerItem() throws Exception {
        Integer bookingId = 1;
        Integer userId = 2;

        Booking booking = Booking.builder()
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

        when(bookingService.getBooking(bookingId, userId)).thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(booking.getStatus()))));
    }

    @Test
    void createBookingWhenBookingValid() throws Exception {
        Integer userId = 2;
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking bookingReq = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking bookingSaved = Booking.builder()
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

        when(bookingService.createBooking(bookingReq)).thenReturn(bookingSaved);

        mockMvc.perform(post("/bookings", bookingRequest)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(bookingRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(bookingSaved.getId())))
                .andExpect(jsonPath("$.booker.id", is(bookingSaved.getBooker().getId())))
                .andExpect(jsonPath("$.item.id", is(bookingSaved.getItem().getId())))
                .andExpect(jsonPath("$.status", is(String.valueOf(bookingSaved.getStatus()))));

    }

    @Test
    void createBookingWhenNotValidDate() throws Exception {
        Integer userId = 2;
        BookingRequest bookingRequest = BookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(3))
                .build();

        Booking bookingReq = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(3))
                .build();

        when(bookingService.createBooking(bookingReq)).thenThrow(new AccessibilityErrorException("Не верные даты"));

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> bookingService.createBooking(bookingReq));

        mockMvc.perform(post("/bookings", bookingRequest)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(bookingRequest))
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

        List<Booking> bookings = new ArrayList<>();

        Booking bookingOne = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
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
                .status(BookingStatus.PAST)
                .build();
        bookings.add(bookingOne);

        Booking bookingTwo = Booking.builder()
                .id(2)
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
        bookings.add(bookingTwo);

        when(bookingService.getBookingsByUserId(userId, state, false, from, size)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingOne.getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingOne.getItem().getId())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOne.getBooker().getId())))
                .andExpect(jsonPath("$[1].id", is(bookingTwo.getId())))
                .andExpect(jsonPath("$[1].item.id", is(bookingTwo.getItem().getId())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingTwo.getBooker().getId())));
    }
}