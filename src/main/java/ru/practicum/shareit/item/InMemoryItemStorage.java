package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final ItemMapper itemMapper;
    private final Map<Integer, Item> items = new HashMap<>();

    private Integer generatorId = 0;

    @Override
    public ItemDto createItem(Item item, Integer userId) {
        item.setId(++generatorId);
        item.setOwner(userId);
        items.put(item.getId(), item);
        log.info("Добавлена вещь {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Integer id) {
        Item serchedItem = items.get(id);
        log.info("Отправлена вещь {}", serchedItem);
        return itemMapper.toItemDto(serchedItem);
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(int userId) {
        log.info("Отправлен список вещей пользователя с id {}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchBySubstring(String str, int userId) {
        if (str.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> foundItems = items.values().stream()
                .filter(item -> item.getAvailable() && (item.getName().toLowerCase().contains(str.toLowerCase())
                        || item.getDescription().toLowerCase().contains(str.toLowerCase())))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
            log.info("Отправлен список вещей, содержащих в своем названии или описании подстроку '{}': {}", str, foundItems);
        return foundItems;
    }

    @Override
    public void deleteItem(Integer id) {
        items.remove(id);
        log.info("Удалена вещь {}", getItemById(id));
    }

    @Override
    public ItemDto updateItem(Integer id, Item item, Integer userId) {
        Item updatebleItem = items.get(id);
        if (updatebleItem == null || userId != updatebleItem.getOwner()) {
            log.warn("Вещь с id {} не найдена, либо пользователь с id {} не является её владельцем", id, userId);
            throw new NotFoundException("Вещь не найден");
        }
        item.setId(id);
        item.setOwner(userId);
        if (item.getName() == null) {
            item.setName(updatebleItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(updatebleItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(updatebleItem.getAvailable());
        }
        items.put(id, item);
        log.info("Данные вещи {} изменились {}", updatebleItem, getItemById(id));
        return getItemById(id);
    }
}
