package ru.practicum.shareit.item.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentRequest;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items/{} от пользователя id {}", id, userId);
        ItemResponse response = itemMapper.itemResponseFromItem(itemService.getItemById(id, userId));
        log.info("Отправлен ответ getItemById /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping
    public List<ItemResponse> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                  @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Пришёл GET запрос /items от пользователя id {}", userId);
        List<ItemResponse> response = itemMapper.itemsForResponse(itemService.findByOwnerId(userId, from, size));
        log.info("Отправлен ответ getAllItemsByUserId /items с телом {}", response);
        return response;
    }

    @PostMapping
    public ItemResponse createItem(@RequestBody ItemRequest item, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items от пользователя id {} с телом {}", userId, item);
        Item response = itemService.createItem(itemMapper.itemFromItemRequest(item, userId));
        log.info("Отправлен ответ createItem /items с телом {}", response);
        return itemMapper.itemForCreate(response);
    }

    @PatchMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Integer id, @RequestBody ItemUpdateRequest item,
                                   @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл PATCH запрос /items/{} от пользователя id {} с телом {}", id, userId, item);
        ItemResponse response = itemMapper.itemResponseFromItem(itemService.updateItem(itemMapper.itemFromUpdate(item, userId, id)));
        log.info("Отправлен ответ updateItem /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemResponse> searchBySubstring(@RequestParam String text,
                                                @RequestHeader(value = "X-Sharer-User-Id") Integer userId,
                                                @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Пришёл GET запрос /items/search от пользователя {} с параметром {}", userId, text);
        List<ItemResponse> response = itemMapper.itemsForResponse(itemService.searchBySubstring(text, text, from, size));
        log.info("Отправлен ответ searchBySubstring /items/search с телом {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /items/{}", id);
        itemService.deleteItem(id);
    }

    @PostMapping("/{id}/comment")
    public CommentResponse createComment(@RequestBody CommentRequest comment, @PathVariable Integer id,
                                         @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items/{}/comment от пользователя {} с телом {}", id, userId, comment);
        CommentResponse response = commentMapper.commentResponse(itemService.addComment(id, commentMapper.commentForCreate(comment), userId));
        log.info("Отправлен ответ addComment /items/{}/comment с телом {}", id, response);
        return response;
    }
}