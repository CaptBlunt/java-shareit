package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    void itemResponseFromItemForUserWhenCommentsEmpty() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setComments(Collections.emptyList());

        ItemResponse result = itemMapper.itemResponseFromItemForUser(item, Collections.emptyList());

        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
    }

    @Test
    void itemFromItemResponse() {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1);
        itemResponse.setName("Test Item");
        itemResponse.setDescription("Test Description");
        itemResponse.setAvailable(true);
        itemResponse.setComments(Collections.emptyList());

        Item item = itemMapper.itemFromItemResponse(itemResponse);

        assertEquals(item.getId(), itemResponse.getId());
        assertEquals(item.getDescription(), itemResponse.getDescription());
    }

    @Test
    void itemForOwnerWhenBookingsExists() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setComments(new ArrayList<>());

        Booking bookingP = Booking.builder()
                .id(1)
                .booker(User.builder()
                        .id(1)
                        .build())
                .build();

        Booking bookingF = Booking.builder()
                .id(2)
                .booker(User.builder()
                        .id(1)
                        .build())
                .build();

        List<Booking> bookings = Collections.singletonList(bookingP);
        List<Booking> bookingsF = Collections.singletonList(bookingF);
        List<Booking> bookingsP = Collections.singletonList(bookingP);

        ItemResponse result = itemMapper.itemForOwner(item, new ArrayList<>(), bookings, bookingsP, bookingsF);

        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
    }

    @Test
    void itemResponseFromItem() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setComments(new ArrayList<>());
        item.setRequest(Request.builder()
                .id(1)
                .build());

        ItemResponse response = itemMapper.itemResponseFromItem(item);

        assertEquals(response.getId(), item.getId());
        assertEquals(response.getRequestId(), item.getRequest().getId());
    }

    @Test
    void itemForRequestFromItem() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        ItemForRequest itemForRequest = itemMapper.itemForRequestFromItem(item);

        assertEquals(item.getId(), itemForRequest.getId());
        assertEquals(item.getRequest(), itemForRequest.getRequestId());
    }

    @Test
    void itemForOwner() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        List<CommentResponse> comments = new ArrayList<>();
        CommentResponse comment1 = new CommentResponse();
        comment1.setId(1);
        comment1.setText("Comment 1");
        comment1.setAuthorName("dasd");
        comment1.setCreated(LocalDateTime.now().minusDays(1));
        comments.add(comment1);


        List<Booking> bookings = Collections.emptyList();
        List<Booking> pastBookings = Collections.emptyList();
        List<Booking> futureBookings = Collections.emptyList();

        ItemResponse result = itemMapper.itemForOwner(item, comments, bookings, pastBookings, futureBookings);

        assertEquals(1, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(result.getComments().get(0), comments.get(0));
    }
}