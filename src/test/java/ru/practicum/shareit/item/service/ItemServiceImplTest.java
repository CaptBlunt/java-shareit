package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.CommentRepository;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentMapper commentMapper;

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

    @Test
    void updateItemWhenUserNotOwner() {
        int userId = 3;

        User user = new User();
        user.setId(1);
        user.setEmail("dsad");
        user.setName("sadasd");

        User user2 = new User();
        user2.setId(3);

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(user2);
        item.setAvailable(true);

        Item itemNew = new Item();
        itemNew.setId(1);
        itemNew.setName("Test Item2");
        itemNew.setDescription("Test Description2");
        itemNew.setOwner(user);
        itemNew.setAvailable(true);

        when(itemRepository.findById(1)).thenReturn(Optional.of(itemNew));

        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(user));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(item));

        assertEquals("Пользователь " + userId + " не является владельцем  вещи " + itemNew.getId(), exception.getMessage());
    }

    @Test
    void updateItemWhenItemValidUserOwnerWithoutComments() {
        User user = new User();
        user.setId(1);
        user.setEmail("dsad");
        user.setName("sadasd");

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(user);
        item.setAvailable(true);

        Item itemNew = new Item();
        itemNew.setId(1);
        itemNew.setName("Test Item2");
        itemNew.setDescription("Test Description2");
        itemNew.setOwner(user);
        itemNew.setAvailable(true);

        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(user));

        when(commentRepository.findByItemIdAndOwnerId(itemNew.getId())).thenReturn(new ArrayList<>());

        when(itemRepository.save(itemNew)).thenReturn(itemNew);

        Item item1 = itemService.updateItem(item);

        assertEquals(item1, itemNew);
    }

    @Test
    void addCommentWhenCommentEmpty() {
        Comment newComment = Comment.builder()
                .text("")
                .build();
        int itemId = 1;
        int userId = 1;
        ValidateException exception = assertThrows(ValidateException.class, () -> itemService.addComment(itemId, newComment, userId));

        assertEquals("Пустой комментарий", exception.getMessage());
    }

    @Test
    void addCommentWhenValidComment() {
        int itemId = 1;
        int userId = 2;
        Comment newComment = Comment.builder()
                .text("Test")
                .build();

        User user = new User();
        user.setId(1);

        User author = new User();
        author.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(user);
        item.setAvailable(true);

        Item itemNew = new Item();
        itemNew.setId(1);
        itemNew.setName("Test Item2");
        itemNew.setDescription("Test Description2");
        itemNew.setOwner(user);
        itemNew.setAvailable(true);

        Comment commentNew = Comment.builder()
                .text(newComment.getText())
                .item(itemNew)
                .authorName(author)
                .created(LocalDateTime.of(2024, 3, 19, 0, 0))
                .build();

        Comment commentNewSave = Comment.builder()
                .id(1)
                .text(newComment.getText())
                .item(itemNew)
                .authorName(author)
                .created(LocalDateTime.of(2024, 3, 19, 0, 0))
                .build();

        List<Booking> bookingsPast = List.of(new Booking());

        List<Booking> bookingsFuture = List.of(new Booking());

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(author.getId(), item.getId())).thenReturn(bookingsPast);

        when(bookingRepository.findByBookerIdAndItemIdFutureBookings(author.getId(), item.getId())).thenReturn(bookingsFuture);

        when(commentMapper.commentFromCommentRequest(commentMapper.commentRequestFromComment(newComment), author, item)).thenReturn(commentNew);

        when(commentRepository.save(commentNew)).thenReturn(commentNewSave);

        Comment test = itemService.addComment(itemId, newComment, userId);

        assertEquals(test, commentNewSave);
    }
}