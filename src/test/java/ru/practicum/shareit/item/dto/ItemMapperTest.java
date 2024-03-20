package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    void itemResponseFromItemForUser() {
        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setComments(new ArrayList<>());

        ItemResponse result = itemMapper.itemResponseFromItemForUser(item, new ArrayList<>());

        assertEquals(result.getId(), item.getId());
        assertEquals(result.getDescription(), item.getDescription());
    }

    @Test
    void itemFromItemResponse() {
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


        List<Booking> bookings = new ArrayList<>();
        List<Booking> pastBookings = new ArrayList<>();
        List<Booking> futureBookings = new ArrayList<>();

        ItemResponse result = itemMapper.itemForOwner(item, comments, bookings, pastBookings, futureBookings);

        assertEquals(1, result.getId());
        assertEquals("Test Item", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(result.getComments().get(0), comments.get(0));
    }
}