package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(Integer userId, PageRequest pageable);

    List<Item> findByNameContainingOrDescriptionContainingIgnoreCase(String str, String str1, PageRequest pageable);

    Item findByRequestId(Integer requestId);
}
