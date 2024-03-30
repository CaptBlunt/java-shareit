package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
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
import java.time.format.DateTimeFormatter;
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


    @BeforeEach
    private void delete() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void testFindByItemIdAndOwnerId() {
        User owner = new User("bob@gamail.com", "Bob");
        userRepository.save(owner);

        User user2 = new User("bob2@gamail.com", "Bob2");
        userRepository.save(user2);

        Item item = new Item("Test", "Test Item", owner, true);
        itemRepository.save(item);

        String str1 = "2024-03-09 12:30";
        String str2 = "2024-03-10 12:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime1 = LocalDateTime.parse(str1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(str2, formatter);

        Comment comment = new Comment("TestTestTest", item, dateTime1, user2);

        commentRepository.save(comment);

        Comment comment2 = new Comment("Test2Test2Test2", item, dateTime2, user2);

        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());

        assertFalse(comments.isEmpty());
        assertEquals(comments.get(0).getItem().getId(), item.getId());
        assertEquals(comments.get(1).getItem().getId(), item.getId());
    }
}
