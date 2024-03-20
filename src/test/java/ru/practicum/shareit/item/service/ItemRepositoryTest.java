package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
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


    @BeforeEach
    private void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findByOwnerId() {
        User user = new User();
        user.setEmail("bob@gamail.com");
        user.setName("Bob");
        userRepository.save(user);

        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        PageRequest page = PageRequest.of(1 / 10, 10);

        List<Item> users = itemRepository.findByOwnerId(user.getId(), page);

        assertFalse(users.isEmpty());
        assertEquals(users.get(0).getOwner().getId(), user.getId());
    }

    @Test
    void findByNameContainingOrDescriptionContainingIgnoreCase() {
        User user = new User();
        user.setEmail("bob@gamail.com");
        user.setName("Bob");
        userRepository.save(user);

        String str = "es";

        Item item = new Item();
        item.setName("Test");
        item.setDescription("Str");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        Item item2 = new Item();
        item2.setName("Str");
        item2.setDescription("Tst");
        item2.setOwner(user);
        item2.setAvailable(true);
        itemRepository.save(item2);

        PageRequest page = PageRequest.of(1 / 10, 10);

        List<Item> items = itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase(str, str, page);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), item.getName());
    }

    @Test
    void findByRequestId() {
        User user = new User();
        user.setEmail("bob@gamail.com");
        user.setName("Bob");
        userRepository.save(user);

        User user2 = new User();
        user2.setEmail("bob2@gamail.com");
        user2.setName("Bob2");
        userRepository.save(user2);

        Request request = new Request();
        request.setDescription("fdasfsaf");
        request.setRequestor(user2);
        request.setCreatedDate(LocalDateTime.now());
        requestRepository.save(request);

        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test");
        item.setOwner(user);
        item.setAvailable(true);
        item.setRequest(request);
        itemRepository.save(item);

        Item item2 = itemRepository.findByRequestId(request.getId());

        assertEquals(item.getId(), item2.getId());
        assertEquals(item.getName(), item2.getName());
    }
}