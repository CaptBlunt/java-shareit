package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemStorage {

    Item createItem(Item item);

    Item getItemById(Integer id);

    List<Item> getAllItemsByUserId(int userId);

    List<Item> searchBySubstring(String str, int userId);

    void deleteItem(Integer id);

    Item updateItem(Integer id, Item item, Integer userId);
}