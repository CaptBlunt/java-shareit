package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestMapperTest {

    private RequestMapper requestMapper;

    @BeforeEach
    void setUp() {
        requestMapper = new RequestMapper();
    }

    @Test
    void requestForUser() {
        Request request = Request.builder()
                .id(1)
                .description("dsa")
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        List<ItemForRequest> items = new ArrayList<>();

        RequestForUser requestForUser = requestMapper.requestForUser(request, items);

        assertEquals(requestForUser.getId(), request.getId());
        assertEquals(requestForUser.getDescription(), request.getDescription());
        assertEquals(requestForUser.getCreated(), request.getCreatedDate());
    }
}