package ru.practicum.shareit.item.storage;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comments.model.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemServiceDao {
    @Transactional
    ItemDto createItem(Item item, Integer userId);

    @Transactional(readOnly = true)
    ItemDto getItemById(Integer id, Integer userId);

    @Transactional(readOnly = true)
    List<ItemDto> findByOwnerId(Integer userId);

    @Transactional(readOnly = true)
    List<ItemDto> searchBySubstring(String str, String str1);

    @Transactional
    void deleteItem(Integer id);

    @Transactional
    ItemDto updateItem(Integer id, Item item, Integer userId);
    @Transactional
    CommentDto addComment(Integer itemId, CommentDto.CommentDtoPost comment, Integer userId);
}
