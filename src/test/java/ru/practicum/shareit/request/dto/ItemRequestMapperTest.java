package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    void setUp() {
        itemRequestMapper = new ItemRequestMapper();
    }

    @Test
    void requestForUser() {
        Request request = Request.builder()
                .id(1)
                .description("dsa")
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        List<ItemForRequest> items = new ArrayList<>();

        UsersItemRequestResponse usersItemRequestResponse = itemRequestMapper.requestForUser(request, items);

        assertEquals(usersItemRequestResponse.getId(), request.getId());
        assertEquals(usersItemRequestResponse.getDescription(), request.getDescription());
        assertEquals(usersItemRequestResponse.getCreated(), request.getCreatedDate());
    }
}