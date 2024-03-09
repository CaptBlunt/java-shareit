package ru.practicum.shareit.item.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.model.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemDao;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items/{} от пользователя id {}", id, userId);
        ItemDto response = itemDao.getItemById(id, userId);
        log.info("Отправлен ответ getItemById /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items от пользователя id {}", userId);
        List<ItemDto> response = itemDao.findByOwnerId(userId);
        log.info("Отправлен ответ getAllItemsByUserId /items с телом {}", response);
        return response;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items от пользователя id {} с телом {}", userId, item);
        ItemDto response = itemDao.createItem(item, userId);
        log.info("Отправлен ответ createItem /items с телом {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id, @RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл PATCH запрос /items/{} от пользователя id {} с телом {}", id, userId, item);
        ItemDto response = itemDao.updateItem(id, item, userId);
        log.info("Отправлен ответ updateItem /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> searchBySubstring(@RequestParam String text,
                                           @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items/search от пользователя {} с параметром {}", userId, text);
        List<ItemDto> response = itemDao.searchBySubstring(text, text);
        log.info("Отправлен ответ searchBySubstring /items/search с телом {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /items/{}", id);
        itemDao.deleteItem(id);
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestBody CommentDto.CommentDtoPost comment, @PathVariable Integer id,
                                 @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items/{}/comment от пользователя {} с телом {}", id, userId, comment);
        CommentDto response = itemDao.addComment(id, comment, userId);
        log.info("Отправлен ответ addComment /items/{}/comment с телом {}", id, response);
        return response;
    }
}