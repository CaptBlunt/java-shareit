package ru.practicum.shareit.item;

import java.util.List;

public interface ItemStorage {

    ItemDto createItem(Item item, Integer userId);

    ItemDto getItemById(Integer id);

    List<ItemDto> getAllItemsByUserId(int userId);

    List<ItemDto> searchBySubstring(String str, int userId);

    void deleteItem(Integer id);

    ItemDto updateItem(Integer id, Item item, Integer userId);
}