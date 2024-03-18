package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    private void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void testFindByItemIdAndOwnerId() {
        User user = new User();
        user.setEmail("bob@gamail.com");
        user.setName("Bob");
        userRepository.save(user);

        User user2 = new User();
        user2.setName("Bob2");
        user2.setEmail("bob2@gamail.com");
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Boby");
        user3.setEmail("boby@gamail.com");
        userRepository.save(user3);

        Item item = new Item();
        item.setName("Test");
        item.setDescription("Test Item");
        item.setOwner(user);
        item.setAvailable(true);
        itemRepository.save(item);

        Comment comment = new Comment();
        comment.setText("TestTestTest");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now().minusDays(1));
        comment.setAuthorName(user2);

        commentRepository.save(comment);

        Comment comment2 = new Comment();
        comment2.setText("Test2Test2Test2");
        comment2.setItem(item);
        comment2.setCreated(LocalDateTime.now().minusHours(2));
        comment2.setAuthorName(user3);

        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByItemIdAndOwnerId(user.getId());

        assertFalse(comments.isEmpty());
        assertEquals(comments.get(0).getItem().getId(), item.getId());
        assertEquals(comments.get(1).getItem().getId(), item.getId());
    }
}
