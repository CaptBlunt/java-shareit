package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentMapper;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
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
        User owner = new User();
        owner.setId(1);
        owner.setEmail("dsad");
        owner.setName("sadasd");

        User author = new User();
        author.setId(7);
        author.setEmail("dsaadsd");
        author.setName("saddasdasd");


        CommentResponse commentResponse = CommentResponse.builder()
                .id(1)
                .text("dasd")
                .authorName("saddasdasd")
                .created(LocalDateTime.now().minusDays(1))
                .build();

        List<CommentResponse> commentResponses = List.of(commentResponse);

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(owner);
        item.setComments(commentResponses);
        item.setAvailable(true);

        List<Item> items = List.of(item);

        Comment comment = Comment.builder()
                .id(1)
                .text("dasd")
                .item(item)
                .authorName(author)
                .created(LocalDateTime.now().minusDays(1))
                .build();


        List<Comment> comments = List.of(comment);

        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByOwnerId(anyInt(), eq(page))).thenReturn(items);

        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(comments);

        when(userRepository.getReferenceById(author.getId())).thenReturn(author);

        when(commentMapper.commentResponseFromComment(eq(comment), eq("saddasdasd"))).thenReturn(commentResponse);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(eq(item), eq(commentResponses)))).thenReturn(item);

        List<Item> result = itemService.findByOwnerId(1, 1, 10);

        assertEquals(result, items);

    }

    @Test
    void getItemByIdWhenUserNotOwner() {
        User owner = new User();
        owner.setId(1);

        User user = new User();
        user.setId(2);

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(owner);
        item.setAvailable(true);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(new ArrayList<>());

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, Collections.emptyList()))).thenReturn(item);

        Item result = itemService.getItemById(item.getId(), user.getId());

        assertEquals(item, result);
    }

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
    void getItemByIdWhenCommentExists() {
        int itemId = 2;
        int userId = 1;

        User owner = User.builder()
                .id(4)
                .email("dsad@dsa.com")
                .name("dasdd")
                .build();

        User author = User.builder()
                .id(1)
                .email("dsad2@dsa.com")
                .name("dasd2d")
                .build();

        Item item = Item.builder()
                .id(2)
                .name("test")
                .description("dasd")
                .owner(owner)
                .available(true)
                .comments(new ArrayList<>())
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("dasd")
                .item(item)
                .authorName(author)
                .created(LocalDateTime.now())
                .build();

        List<Comment> comments = List.of(comment);

        CommentResponse commentResponse = CommentResponse.builder()
                .id(1)
                .text("dasd")
                .authorName("dasd2d")
                .created(comment.getCreated())
                .build();

        List<CommentResponse> commentResponses = List.of(commentResponse);

        ItemResponse itemResponse = ItemResponse.builder()
                .id(2)
                .name("test")
                .description("dasd")
                .available(true)
                .comments(commentResponses)
                .build();

        Item itemResponse1 = Item.builder()
                .id(2)
                .name("test")
                .description("dasd")
                .owner(owner)
                .available(true)
                .comments(commentResponses)
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(comments);

        when(userRepository.getReferenceById(comment.getAuthorName().getId())).thenReturn(author);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, commentResponses))).thenReturn(itemResponse1);

        Item result = itemService.getItemById(itemId, userId);

        assertEquals(result.getId(), itemResponse.getId());
    }

    @Test
    void updateItemWhenCommentsNotEmpty() {
        User author = User.builder()
                .id(1)
                .email("dsad2@dsa.com")
                .name("dasd2d")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("Test Item")
                .description("Sample description")
                .available(true)
                .owner(User.builder().id(1).build())
                .build();

        List<Comment> comments = new ArrayList<>();
        Comment comment1 = Comment.builder().id(1).authorName(User.builder().id(2).build()).text("Comment 1").build();
        comments.add(comment1);

        List<CommentResponse> commentResponses = new ArrayList<>();
        CommentResponse response1 = new CommentResponse();
        commentResponses.add(response1);
        Item updatedItem = Item.builder()
                .id(1)
                .name("Updated Item")
                .description("Updated description")
                .available(false)
                .owner(User.builder().id(1).build())
                .comments(commentResponses)
                .build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(item.getOwner().getId())).thenReturn(Optional.of(item.getOwner()));
        when(commentRepository.findByItemIdAndOwnerId(item.getId())).thenReturn(comments);
        when(userRepository.getReferenceById(comment1.getAuthorName().getId())).thenReturn(author);
        when(commentMapper.commentResponseFromComment(any(), any())).thenReturn(response1);

        when(itemRepository.save(any())).thenReturn(updatedItem);

        Item result = itemService.updateItem(item);

        assertEquals(updatedItem.getName(), result.getName());
        assertEquals(updatedItem.getDescription(), result.getDescription());
        assertEquals(updatedItem.getAvailable(), result.getAvailable());
        assertEquals(commentResponses.size(), result.getComments().size());
    }



    @Test
    void getItemByIdWhenLastBookingAndNextBookingId() {
        int itemId = 2;
        int userId = 1;

        User owner = User.builder()
                .id(1)
                .email("dsad@dsa.com")
                .name("dasdd")
                .build();

        User booker = User.builder()
                .id(6)
                .email("dsad2@dsa.com")
                .name("dasdd")
                .build();

        Item item = Item.builder()
                .id(2)
                .name("test")
                .description("dasd")
                .owner(owner)
                .available(true)
                .comments(new ArrayList<>())
                .build();

        Booking bookingLast = Booking.builder()
                .id(2)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking bookingFuture = Booking.builder()
                .id(3)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> bookings = List.of(bookingLast, bookingFuture);
        List<Booking> pastBookings = List.of(bookingLast);
        List<Booking> futureBookings = List.of(bookingFuture);

        Item itemResponse = Item.builder()
                .id(2)
                .name("test")
                .description("dasd")
                .available(true)
                .comments(new ArrayList<>())
                .lastBooking(ItemResponse.ItemForOwner.builder()
                        .id(2)
                        .bookerId(6)
                        .build())
                .nextBooking(ItemResponse.ItemForOwner.builder()
                        .id(3)
                        .bookerId(6)
                        .build())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(item.getId())).thenReturn(bookings);
        when(bookingRepository.findByOwnerIdAndItemIdPastBookings(item.getOwner().getId(), item.getId())).thenReturn(pastBookings);
        when(bookingRepository.findByOwnerIdAndItemIdFutureBookings(item.getOwner().getId(), item.getId())).thenReturn(futureBookings);
        when(itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, new ArrayList<>(), bookings, pastBookings, futureBookings))).thenReturn(itemResponse);

        Item result = itemService.getItemById(itemId, userId);
        assertEquals(result, itemResponse);
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
    void findByOwnerIdWhenCommentExists() {
        User user = new User();
        user.setId(1);
        user.setEmail("dsad");
        user.setName("sadasd");

        User author = new User();
        author.setId(7);
        author.setEmail("dsaadsd");
        author.setName("saddasdasd");

        Item item = new Item();
        item.setId(1);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setOwner(user);
        item.setAvailable(true);

        List<Item> items = List.of(item);

        Comment comment = Comment.builder()
                .id(1)
                .text("dasd")
                .item(item)
                .authorName(author)
                .created(LocalDateTime.now().minusDays(1))
                .build();

        List<Comment> comments = List.of(comment);

        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByOwnerId(anyInt(), eq(page))).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(new ArrayList<>());

        when(itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), new ArrayList<>()))).thenReturn(item);

        List<Item> result = itemService.findByOwnerId(1, 1, 10);

        assertEquals(result, items);
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

    @Test
    void searchItemsBySubstringWhenSubstringEmpty() {
        when(itemService.searchBySubstring("test", "test", 0, 10)).thenReturn(new ArrayList<>());

        List<Item> items = itemService.searchBySubstring("test", "test", 0, 10);

        assertEquals(new ArrayList<>(), items);
    }

    @Test
    void searchItemsBySubstringWhenSubstringTes() {
        Item itemOne = new Item();
        itemOne.setId(1);
        itemOne.setName("Test Item");
        itemOne.setDescription("Test Description");
        itemOne.setAvailable(true);

        Item itemTwo = new Item();
        itemTwo.setId(2);
        itemTwo.setName("Test Item2");
        itemTwo.setDescription("Test Description2");
        itemTwo.setAvailable(true);

        List<Item> items = Arrays.asList(itemOne, itemTwo);

        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase("tes", "tes", page)).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(new ArrayList<>());

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(any(Item.class), anyList()))).thenReturn(itemOne, itemTwo);

        List<Item> itemsResult = itemService.searchBySubstring("tes", "tes", 0, 10);

        assertEquals(itemsResult.size(), items.size());
        assertEquals(itemsResult.get(0), items.get(0));
        assertEquals(itemsResult.get(1), items.get(1));
    }

    @Test
    void searchItemsBySubstringWhenSubstringTesAndCommentsExists() {
        Item itemOne = new Item();
        itemOne.setId(1);
        itemOne.setName("Test Item");
        itemOne.setDescription("Test Description");
        itemOne.setAvailable(true);

        Item itemTwo = new Item();
        itemTwo.setId(2);
        itemTwo.setName("Test Item2");
        itemTwo.setDescription("Test Description2");
        itemTwo.setAvailable(true);

        User author = new User();
        author.setId(1);

        Comment comment = Comment.builder()
                .id(1)
                .text("dadas")
                .item(itemTwo)
                .authorName(author)
                .created(LocalDateTime.of(2024, 3, 19, 0, 0))
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(1)
                .text("dadas")
                .authorName(author.getName())
                .created(LocalDateTime.of(2024, 3, 19, 0, 0))
                .build();

        List<CommentResponse> commentResponses = Collections.singletonList(response);

        List<Item> items = Arrays.asList(itemTwo);

        List<Comment> comments = Collections.singletonList(comment);

        PageRequest page = PageRequest.of(1 / 10, 10);

        when(itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase("tes", "tes", page)).thenReturn(items);
        when(commentRepository.findByItemIdAndOwnerId(anyInt())).thenReturn(comments);

        when(userRepository.getReferenceById(author.getId())).thenReturn(author);

        when(commentMapper.commentResponseFromComment(comment, author.getName())).thenReturn(response);

        when(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(itemTwo, commentResponses))).thenReturn(itemTwo);

        List<Item> itemsResult = itemService.searchBySubstring("tes", "tes", 0, 10);

        assertEquals(itemsResult.size(), items.size());
        assertEquals(itemsResult.get(0), items.get(0));
    }

    @Test
    void validationItem() {
        Item item = new Item();
        item.setName("");
        item.setDescription("Test Description2");
        item.setAvailable(true);

        ValidateException exception = assertThrows(ValidateException.class, () -> itemService.validateItem(item));

        assertEquals("Некорректно указаны данные", exception.getMessage());
    }

    @Test
    void addCommentWhenUserNotBooking() {
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(author.getId(), item.getId())).thenThrow(new AccessibilityErrorException("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось"));

        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> itemService.addComment(itemId, commentNew, author.getId()));

        assertEquals("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось", exception.getMessage());
    }

    @Test
    void addCommentWhenUserBookingInFuture() {
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

        List<Booking> bookingsPast = Collections.emptyList();

        List<Booking> bookingsFuture = List.of(new Booking());

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdPastBookings(author.getId(), item.getId())).thenReturn(bookingsPast);

        when(bookingRepository.findByBookerIdAndItemIdFutureBookings(author.getId(), item.getId())).thenReturn(bookingsFuture);


        AccessibilityErrorException exception = assertThrows(AccessibilityErrorException.class, () -> itemService.addComment(itemId, newComment, userId));

        assertEquals("Пользователь забронировал эту вещь в будущем", exception.getMessage());
    }
}
