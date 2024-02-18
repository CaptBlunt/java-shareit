package ru.practicum.shareit.item.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Integer id) {
        log.info("Пришёл GET запрос /items/{}", id);
        ItemDto response = itemService.getItemById(id);
        log.info("Отправлен ответ getItemById /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items от пользователя id {}", userId);
        List<ItemDto> response = itemService.getAllItemsByUserId(userId);
        log.info("Отправлен ответ getAllItemsByUserId /items с телом {}", response);
        return response;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items от пользователя id {} с телом {}", userId, item);
        ItemDto response = itemService.createItem(item, userId);
        log.info("Отправлен ответ createItem /items с телом {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id, @RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл PATCH запрос /items/{} от пользователя id {} с телом {}", id, userId, item);
        ItemDto response = itemService.updateItem(id, item, userId);
        log.info("Отправлен ответ updateItem /items/{} с телом {}", id, response);
        return response;
    }

    @GetMapping("/search")
    public List<ItemDto> searchBySubstring(@RequestParam String text,
                                           @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items/search с параметром {}", text);
        List<ItemDto> response = itemService.searchBySubstring(text, userId);
        log.info("Отправлен ответ searchBySubstring /items/search с телом {}", response);
        return response;

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /items/{}", id);
        itemService.deleteItem(id);
    }
}