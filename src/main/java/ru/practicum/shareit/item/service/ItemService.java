package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    @Transactional
    Item createItem(Item item);

    @Transactional(readOnly = true)
    Item getItemById(Integer id, Integer userId);

    @Transactional(readOnly = true)
    List<Item> findByOwnerId(Integer userId, Integer from, Integer size);

    @Transactional(readOnly = true)
    List<Item> searchBySubstring(String str, String str1, Integer from, Integer size);

    @Transactional
    void deleteItem(Integer id);

    @Transactional
    Item updateItem(Item item);

    @Transactional
    Comment addComment(Integer itemId, Comment comment, Integer userId);
}
