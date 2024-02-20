package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidateItemException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final InMemoryItemStorage inMemoryItemStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final ItemMapper itemMapper;

    public ItemDto createItem(Item item, Integer userId) {
        validateItem(item);
        item.setOwner(userId);
        inMemoryUserStorage.getUserById(userId);
        return itemMapper.toItemDto(inMemoryItemStorage.createItem(item));
    }

    public ItemDto getItemById(Integer id) {
        return itemMapper.toItemDto(inMemoryItemStorage.getItemById(id));
    }

    public List<ItemDto> getAllItemsByUserId(int userId) {
        return inMemoryItemStorage.getAllItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchBySubstring(String str, int userId) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        return inMemoryItemStorage.searchBySubstring(str, userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(Integer id) {
        inMemoryItemStorage.deleteItem(id);
    }

    public ItemDto updateItem(Integer id, Item item, Integer userId) {
        inMemoryUserStorage.getUserById(userId);
        return itemMapper.toItemDto(inMemoryItemStorage.updateItem(id, item, userId));
    }

    public void validateItem(Item item) {
        String itemName = item.getName();
        String itemDesc = item.getDescription();
        Boolean itemAvail = item.getAvailable();

        if ((itemAvail == null) || (itemDesc == null || itemDesc.isEmpty()) || (itemName == null || itemName.isEmpty())) {
            log.info("Ошибка валидации вещи");
            throw new ValidateItemException("Некорректно указаны данные");
        }
    }
}
