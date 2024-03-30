package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequest;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@Min(1) @PathVariable Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid ItemRequest item, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@Min(1) @PathVariable Integer id, @RequestBody ItemUpdateRequest item,
                                   @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return itemClient.updateItem(id, item, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBySubstring(@RequestParam String text,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemClient.searchBySubstring(userId, text, from, size);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@Min(1) @PathVariable Integer id) {
        itemClient.deleteItem(id);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid CommentRequest comment, @Min(1) @PathVariable Integer id,
                                                @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return itemClient.createComment(comment, id, userId);
    }
}
