package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidateItemException;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public ItemDto createItem(Item item, Integer userId) {
        validateItem(item);
        inMemoryUserStorage.getUserById(userId);
        return inMemoryItemStorage.createItem(item, userId);
    }

    public ItemDto getItemById(Integer id) {
        return inMemoryItemStorage.getItemById(id);
    }

    public List<ItemDto> getAllItemsByUserId(int userId) {
        return inMemoryItemStorage.getAllItemsByUserId(userId);
    }

    public List<ItemDto> searchBySubstring(String str, int userId) {
        return inMemoryItemStorage.searchBySubstring(str, userId);
    }

    public void deleteItem(Integer id) {
        inMemoryItemStorage.deleteItem(id);
    }

    public ItemDto updateItem(Integer id, Item item, Integer userId) {
        inMemoryUserStorage.getUserById(userId);
        return inMemoryItemStorage.updateItem(id, item, userId);
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
