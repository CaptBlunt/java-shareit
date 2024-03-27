package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.CommentRepository;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    User user = new User("bob@gamail.com", "Bob");

    Item itemTwo = new Item("Str", "Tst", user, true);

    User userTwo = new User("bob2@gamail.com", "Bob2");

    Request request = new Request("test", userTwo, LocalDateTime.now());

    Item item = new Item("Test", "Test", user, true, request);


    @BeforeEach
    private void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();

        userRepository.save(user);
        userRepository.save(userTwo);
        itemRepository.save(item);
        itemRepository.save(itemTwo);
        requestRepository.save(request);
    }

    @Test
    void findByOwnerId() {
        PageRequest page = PageRequest.of(1 / 10, 10);

        List<Item> users = itemRepository.findByOwnerId(user.getId(), page);

        assertFalse(users.isEmpty());
        assertEquals(users.get(0).getOwner().getId(), user.getId());
    }

    @Test
    void findByNameContainingOrDescriptionContainingIgnoreCase() {
        String str = "es";

        PageRequest page = PageRequest.of(1 / 10, 10);

        List<Item> items = itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase(str, str, page);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), item.getName());
    }

    @Test
    void findByRequestId() {
        Item item2 = itemRepository.findByRequestId(request.getId());

        assertEquals(item.getId(), item2.getId());
        assertEquals(item.getName(), item2.getName());
    }
}