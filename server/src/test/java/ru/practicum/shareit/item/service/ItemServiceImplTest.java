package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.CommentRepository;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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

    @Mock
    private ItemMapper itemMapper;


    User user = new User(1, "dsaadsd", "author");
    User userTwo = new User(7, "dsaadsd", "author");

    CommentResponse commentResponse = new CommentResponse(1, "dasd", "name", LocalDateTime.now().minusDays(1));

    List<CommentResponse> commentResponses = List.of(commentResponse);

    Item item = new Item(1, "Test Item", "Test Description", user, commentResponse, true);
    Item itemUpd = new Item(1, "Test Item", "Test Description", userTwo, commentResponse, true);

    List<Item> items = List.of(item);

    Comment comment = new Comment(1, "dasd", item, userTwo, LocalDateTime.now().minusDays(1));
    List<Comment> comments = List.of(comment);

    Booking bookingLast = new Booking();
    Booking bookingFuture = new Booking();

    List<Booking> bookings = List.of(bookingLast, bookingFuture);
    List<Booking> pastBookings = List.of(bookingLast);
    List<Booking> futureBookings = List.of(bookingFuture);

    @Test
    void paginationNotValid() {
        ValidateException exception = assertThrows(ValidateException.class, () -> itemService.findByOwnerId(1, -1, 10));
        assertEquals("Проверьте указанные параметры", exception.getMessage());
    }

    @Test
    void pagination() {
        PageRequest pageRequest = PageRequest.of(0 / 1000, 1000);

        PageRequest result = itemService.pagination(null, null);

        assertEquals(result, pageRequest);
    }

    @Test
    void searchWhenSubstringEmpty() {
        List<Item> emptyList = itemService.searchBySubstring("", "", 10, 10);

        assertEquals(emptyList, new ArrayList<>());
    }

    @Test
    void getAllItemsByUserId() {
        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByOwnerId(anyInt(), eq(page))).thenReturn(items);

        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(comments);

        when(userRepository.getReferenceById(userTwo.getId())).thenReturn(userTwo);

        when(commentMapper.commentResponseFromComment(eq(comment), eq("author"))).thenReturn(commentResponse);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(eq(item), eq(commentResponses)))).thenReturn(item);

        List<Item> result = itemService.findByOwnerId(1, 1, 10);

        assertEquals(result, items);
    }

    @Test
    void getItemByIdWhenUserNotOwner() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(Collections.emptyList());

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, Collections.emptyList()))).thenReturn(item);

        Item result = itemService.getItemById(item.getId(), userTwo.getId());

        assertEquals(item, result);
    }

    @Test
    void createItemWhenItemValid() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        when(itemRepository.save(item)).thenReturn(item);

        Item createdItem = itemService.createItem(item);

        verify(userRepository).findById(1);
        verify(itemRepository).save(item);
        assertEquals(item, createdItem);
    }

    @Test
    void getItemByIdWhenCommentExists() {
        int itemId = 2;
        int userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(comments);

        when(userRepository.getReferenceById(comment.getAuthorName().getId())).thenReturn(userTwo);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, commentResponses))).thenReturn(item);

        Item result = itemService.getItemById(itemId, userId);

        assertEquals(result.getId(), item.getId());
    }

    @Test
    void updateItemWhenCommentsNotEmpty() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(comments);
        when(userRepository.getReferenceById(comment.getAuthorName().getId())).thenReturn(userTwo);
        when(commentMapper.commentResponseFromComment(any(), any())).thenReturn(commentResponse);

        when(itemRepository.save(any())).thenReturn(item);

        Item result = itemService.updateItem(item);

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(commentResponses.size(), result.getComments().size());
    }


    @Test
    void getItemByIdWhenLastBookingAndNextBookingId() {
        int itemId = 2;
        int userId = 1;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(item.getId())).thenReturn(bookings);
        when(bookingRepository.findByOwnerIdAndItemIdPastBookings(item.getOwner().getId(), item.getId())).thenReturn(pastBookings);
        when(bookingRepository.findByOwnerIdAndItemIdFutureBookings(item.getOwner().getId(), item.getId())).thenReturn(futureBookings);
        when(itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, Collections.emptyList(), bookings, pastBookings, futureBookings))).thenReturn(item);

        Item result = itemService.getItemById(itemId, userId);
        assertEquals(result, item);
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
        int userId = 1;

        when(itemRepository.findById(1)).thenReturn(Optional.of(itemUpd));

        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(user));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.updateItem(item));

        assertEquals("Пользователь " + userId + " не является владельцем  вещи " + itemUpd.getId(), exception.getMessage());
    }

    @Test
    void findByOwnerIdWhenCommentExists() {
        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByOwnerId(anyInt(), eq(page))).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(new ArrayList<>());

        when(itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))).thenReturn(item);

        List<Item> result = itemService.findByOwnerId(1, 1, 10);

        assertEquals(result, items);
    }

    @Test
    void updateItemWhenItemValidUserOwnerWithoutComments() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(user));

        when(commentRepository.findByItemIdAndOwnerId(itemUpd.getId())).thenReturn(Collections.emptyList());

        when(itemRepository.save(itemUpd)).thenReturn(itemUpd);

        Item itemSaved = itemService.updateItem(item);

        assertEquals(itemSaved, itemUpd);
    }

    @Test
    void addCommentWhenCommentEmpty() {
        int itemId = 1;
        int userId = 1;

        Comment newComment = new Comment();
                newComment.setText("");

        ValidateException exception = assertThrows(ValidateException.class, () -> itemService.addComment(itemId, newComment, userId));

        assertEquals("Пустой комментарий", exception.getMessage());
    }

    @Test
    void addCommentWhenValidComment() {
        int itemId = 1;
        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(userTwo));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(userTwo.getId(), item.getId())).thenReturn(pastBookings);

        when(bookingRepository.findByBookerIdAndItemIdFutureBookings(userTwo.getId(), item.getId())).thenReturn(futureBookings);

        when(commentMapper.commentFromCommentRequest(commentMapper.commentRequestFromComment(comment), userTwo, item)).thenReturn(comment);

        when(commentRepository.save(comment)).thenReturn(comment);

        Comment test = itemService.addComment(itemId, comment, userId);

        assertEquals(test, comment);
    }

    @Test
    void searchItemsBySubstringWhenSubstringEmpty() {
        when(itemService.searchBySubstring("test", "test", 0, 10)).thenReturn(new ArrayList<>());

        List<Item> items = itemService.searchBySubstring("test", "test", 0, 10);

        assertEquals(Collections.emptyList(), items);
    }

    @Test
    void searchItemsBySubstringWhenSubstringTes() {
        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase("tes", "tes", page)).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(Collections.emptyList());

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(any(Item.class), anyList()))).thenReturn(item);

        List<Item> itemsResult = itemService.searchBySubstring("tes", "tes", 0, 10);

        assertEquals(itemsResult.size(), items.size());
        assertEquals(itemsResult.get(0), items.get(0));
    }

    @Test
    void searchItemsBySubstringWhenSubstringTesAndCommentsExists() {
        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase("tes", "tes", page)).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(comments);

        when(userRepository.getReferenceById(userTwo.getId())).thenReturn(userTwo);

        when(commentMapper.commentResponseFromComment(comment, userTwo.getName())).thenReturn(commentResponse);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, commentResponses))).thenReturn(item);

        List<Item> itemsResult = itemService.searchBySubstring("tes", "tes", 0, 10);

        assertEquals(itemsResult.size(), items.size());
        assertEquals(itemsResult.get(0), items.get(0));
    }

    @Test
    void addCommentWhenUserNotBooking() {
        int itemId = 1;
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(user.getId(), item.getId())).thenThrow(new AccessibilityErrorException("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось"));

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> itemService.addComment(itemId, comment, user.getId()));

        assertEquals("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось", exception.getMessage());
    }

    @Test
    void addCommentWhenUserBookingInFuture() {
        int itemId = 1;
        int userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.of(userTwo));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(userTwo.getId(), item.getId())).thenReturn(Collections.emptyList());

        when(bookingRepository.findByBookerIdAndItemIdFutureBookings(userTwo.getId(), item.getId())).thenReturn(futureBookings);


        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> itemService.addComment(itemId, comment, userId));

        assertEquals("Пользователь забронировал эту вещь в будущем", exception.getMessage());
    }
}
