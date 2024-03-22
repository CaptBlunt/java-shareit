package ru.practicum.shareit.item.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private CommentMapper commentMapper;

    int itemId = 1;
    int userId = 2;
    ItemResponse itemResponse = ItemResponse.builder()
            .id(itemId)
            .name("Test")
            .description("Test")
            .available(true)
            .build();

    ItemRequest itemReq = ItemRequest.builder()
            .name("test")
            .description("test")
            .available(true)
            .build();

    Item item = Item.builder()
            .id(itemId)
            .name("Test")
            .description("Test")
            .available(true)
            .build();

    Item itemTwo = Item.builder()
            .id(2)
            .description("test1")
            .build();
    List<Item> items = List.of(item, itemTwo);

    CommentRequest request = CommentRequest.builder()
            .text("test")
            .build();

    CommentResponse response = CommentResponse.builder()
            .id(1)
            .authorName("name")
            .text("test")
            .created(LocalDateTime.now())
            .build();


    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(itemId, userId)).thenReturn(item);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(itemResponse.getId())))
                .andExpect(jsonPath("$.name", is(itemResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponse.getAvailable())));
    }

    @Test
    void createItemWhenItemValid() throws Exception {
        when(itemService.createItem(any(Item.class))).thenReturn(item);

        mockMvc.perform(post("/items", itemReq)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void updateItemWhenUserNotTheOwnerItem() throws Exception {
        when(itemService.updateItem(item)).thenThrow(new NotFoundException("Пользователь " + userId + " не является владельцем  вещи " + itemId));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(item));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error", is(exception.getMessage())));
    }

    @Test
    void getAllItemsByUserIdWhenNotParameters() throws Exception {
        Integer userId = 1;
        Integer from = null;
        Integer size = null;

        when(itemService.findByOwnerId(userId, from, size)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(item.getId())))
                .andExpect(jsonPath("$[1].id", is(itemTwo.getId())))
                .andExpect(jsonPath("$[1].description", is(itemTwo.getDescription())));
    }

    @Test
    void getListItemsContainingStringInTheNameOrDescription() throws Exception {
        String text = "e";
        Integer userId = 1;
        Integer from = 0;
        Integer size = 10;

        when(itemService.searchBySubstring(text, text, from, size)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(item.getId())))
                .andExpect(jsonPath("$[1].id", is(itemTwo.getId())))
                .andExpect(jsonPath("$[1].description", is(itemTwo.getDescription())));
    }

    @Test
    void deleteItemById() throws Exception {
        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(anyInt())))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        when(commentMapper.commentResponse(itemService.addComment(itemId, commentMapper.commentForCreate(request), userId))).thenReturn(response);

        mockMvc.perform(post("/items/{id}/comment", itemId, request)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(response.getId())))
                .andExpect(jsonPath("$.authorName", is(response.getAuthorName())))
                .andExpect(jsonPath("$.text", is(response.getText())));
    }
}
