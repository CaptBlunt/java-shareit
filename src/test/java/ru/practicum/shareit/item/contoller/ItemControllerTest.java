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
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void getItemById() throws Exception {
        int itemId = 1;
        int userId = 2;

        ItemResponse itemResponse = ItemResponse.builder()
                .id(itemId)
                .name("Test")
                .description("Test")
                .available(true)
                .build();

        Item itemFromService = Item.builder()
                .id(itemId)
                .name("Test")
                .description("Test")
                .available(true)
                .build();

        when(itemService.getItemById(1, userId)).thenReturn(itemFromService);

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
        Integer userId = 2;

        User owner = new User();
        owner.setId(1);

        ItemRequest itemReq = new ItemRequest();
        itemReq.setName("test");
        itemReq.setDescription("test");
        itemReq.setAvailable(true);

        Item newItem = new Item();
        newItem.setName("test");
        newItem.setDescription("test");
        newItem.setAvailable(true);

        Item itemSaved = new Item();
        itemSaved.setId(1);
        itemSaved.setName("test");
        itemSaved.setDescription("test");
        itemSaved.setAvailable(true);

        when(itemService.createItem(newItem)).thenReturn(itemSaved);

        mockMvc.perform(post("/items", itemReq)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(itemSaved.getId())))
                .andExpect(jsonPath("$.name", is(itemSaved.getName())))
                .andExpect(jsonPath("$.description", is(itemSaved.getDescription())))
                .andExpect(jsonPath("$.available", is(itemSaved.getAvailable())));
    }

    @Test
    void updateItemWhenUserNotTheOwnerItem() throws Exception {
        Integer userId = 1;
        Integer itemId = 1;

        User owner = new User();
        owner.setId(2);

        ItemUpdateRequest itemReq = new ItemUpdateRequest();
        itemReq.setId(itemId);
        itemReq.setName("test");
        itemReq.setDescription("test");
        itemReq.setAvailable(true);

        Item newItem = new Item();
        newItem.setId(itemId);
        newItem.setName("test");
        newItem.setDescription("test");
        newItem.setAvailable(true);
        newItem.setOwner(owner);

        when(itemService.updateItem(newItem)).thenThrow(new NotFoundException("Пользователь " + userId + " не является владельцем  вещи " + itemId));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(newItem));

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(itemReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error", is(exception.getMessage())));
    }

    @Test
    void getAllItemsByUserId() throws Exception {
        Integer userId = 1;
        Integer from = null;
        Integer size = null;

        List<Item> items = new ArrayList<>();

        Item item = new Item();
        item.setId(1);
        item.setName("test");
        items.add(item);

        Item item1 = new Item();
        item1.setId(2);
        item1.setDescription("test1");
        items.add(item1);

        when(itemService.findByOwnerId(userId, from, size)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(item.getId())))
                .andExpect(jsonPath("$[1].id", is(item1.getId())))
                .andExpect(jsonPath("$[1].description", is(item1.getDescription())));
    }

    @Test
    void getListItemsContainingStringInTheNameOrDescription() throws Exception {
        String text = "e";
        Integer userId = 1;
        Integer from = 0;
        Integer size = 10;

        List<Item> items = new ArrayList<>();

        Item item = new Item();
        item.setId(1);
        item.setName("test");
        items.add(item);

        Item item1 = new Item();
        item1.setId(2);
        item1.setDescription("test1");
        items.add(item1);

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
                .andExpect(jsonPath("$[1].id", is(item1.getId())))
                .andExpect(jsonPath("$[1].description", is(item1.getDescription())));
    }

    @Test
    void deleteItemById() throws Exception {
        Integer itemId = 1;
        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(anyInt())))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        Integer userId = 2;
        int itemId = 2;

        CommentRequest request = CommentRequest.builder()
                .text("dasd")
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(1)
                .authorName("dad")
                .text("dasd")
                .created(LocalDateTime.now())
                .build();

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
