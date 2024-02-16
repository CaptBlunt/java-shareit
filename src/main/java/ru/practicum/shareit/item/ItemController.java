package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /items");
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /items с телом {}", item);
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable Integer id, @RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл PATCH запрос /items/{} с телом {}", id, item);
        return itemService.updateItem(id, item, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchBySubstring(@RequestParam String text,
                                           @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /search с параметром {}", text);
        return itemService.searchBySubstring(text, userId);

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /items{}", id);
        itemService.deleteItem(id);
    }
}