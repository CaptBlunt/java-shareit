package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void createItemWhenItemValid() {
        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(user);
        item.setAvailable(true);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Item savedItem = new Item();
        savedItem.setName("Test Item");
        savedItem.setDescription("Test Description");
        savedItem.setOwner(user);

        when(itemRepository.save(item)).thenReturn(savedItem);

        Item createdItem = itemService.createItem(item);

        verify(userRepository).findById(1);
        verify(itemRepository).save(item);
        assertEquals(savedItem, createdItem);
    }

    @Test
    void getItemByIdWhenNotFoundItem() {
        Integer itemId = 1;
        Integer userId = 1;

        when(itemRepository.findById(itemId)).thenThrow(new NotFoundException("Вещь не найдена"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, userId));

        assertEquals("Вещь не найдена", exception.getMessage());
    }
}