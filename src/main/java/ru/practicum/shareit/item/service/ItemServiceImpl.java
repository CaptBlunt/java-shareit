package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


    public PageRequest pagination(Integer from, Integer size) {
        if (from == null || from == 0) {
            from = 0;
        }
        if (size == null) {
            size = 1000;
        }
        if ((from < 0 || size < 0) || (size == 0)) {
            throw new ValidateException("Проверьте указанные параметры");
        }
        return PageRequest.of(from / size, size);
    }

    @Override
    @Transactional
    public Item createItem(Item item) {
        validateItem(item);
        userRepository.findById(item.getOwner().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItemById(Integer id, Integer userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
        List<CommentResponse> commentResponses = new ArrayList<>();

        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                User user = userRepository.getReferenceById(comment.getAuthorName().getId());
                CommentResponse dto = commentMapper.commentResponseFromComment(comment, user.getName());
                commentResponses.add(dto);
            }
        }
        if (!item.getOwner().getId().equals(userId)) {
            return itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, commentResponses));

        } else {
            List<Booking> bookings = bookingRepository.findByItemId(item.getId());
            List<Booking> pastBookings = bookingRepository.findByOwnerIdAndItemIdPastBookings(item.getOwner().getId(), item.getId());
            List<Booking> futureBookings = bookingRepository.findByOwnerIdAndItemIdFutureBookings(item.getOwner().getId(), item.getId());

            return itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, commentResponses, bookings, pastBookings, futureBookings));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByOwnerId(Integer userId, Integer from, Integer size) {
        PageRequest pageable = pagination(from, size);
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);
        return items.stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
                    List<CommentResponse> commentResponses = new ArrayList<>();

                    if (!comments.isEmpty()) {

                        for (Comment comment : comments) {
                            User user = userRepository.getReferenceById(comment.getAuthorName().getId());
                            CommentResponse dto = commentMapper.commentResponseFromComment(comment, user.getName());
                            commentResponses.add(dto);
                        }
                    }
                    List<Booking> bookings = bookingRepository.findByItemId(item.getId());
                    List<Booking> pastBookings = bookingRepository.findByOwnerIdAndItemIdPastBookings(item.getOwner().getId(), item.getId());
                    List<Booking> futureBookings = bookingRepository.findByOwnerIdAndItemIdFutureBookings(item.getOwner().getId(), item.getId());

                    return itemMapper.itemFromItemResponse(itemMapper.itemForOwner(item, commentResponses, bookings, pastBookings, futureBookings));
                })
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> searchBySubstring(String str, String str1, Integer from, Integer size) {
        if (str.isEmpty()) {
            return new ArrayList<>();
        }
        PageRequest pageable = pagination(from, size);

        List<Item> items = itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase(str, str1, pageable);

        List<Item> itemsResponse = new ArrayList<>();

        for (Item item : items) {
            if (item.getAvailable()) {
                List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
                if (comments.isEmpty()) {
                    itemsResponse.add(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, new ArrayList<>())));
                } else {
                    List<CommentResponse> dtos = new ArrayList<>();

                    for (Comment comment : comments) {
                        User user = userRepository.getReferenceById(comment.getAuthorName().getId());
                        CommentResponse dto = commentMapper.commentResponseFromComment(comment, user.getName());
                        dtos.add(dto);
                    }
                    itemsResponse.add(itemMapper.itemFromItemResponse(itemMapper.itemResponseFromItemForUser(item, dtos)));
                }
            }
        }
        return itemsResponse;
    }

    @Override
    @Transactional
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {
        Item itemUpd = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        userRepository.findById(item.getOwner().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!item.getOwner().getId().equals(itemUpd.getOwner().getId())) {
            throw new NotFoundException("Пользователь " + item.getOwner().getId() + " не является владельцем  вещи " + itemUpd.getId());
        }

        if (item.getName() == null) {
            item.setName(itemUpd.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemUpd.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(itemUpd.getAvailable());
        }

        validateItem(item);

        itemUpd.setName(item.getName());
        itemUpd.setDescription(item.getDescription());
        itemUpd.setAvailable(item.getAvailable());

        List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
        if (comments.isEmpty()) {
            itemUpd.setComments(new ArrayList<>());
        } else {
            List<CommentResponse> commentResponses = new ArrayList<>();

            for (Comment comment : comments) {
                User commentFromUser = userRepository.getReferenceById(comment.getAuthorName().getId());
                CommentResponse dto = commentMapper.commentResponseFromComment(comment, commentFromUser.getName());
                commentResponses.add(dto);
            }
            itemUpd.setComments(commentResponses);
        }
        return itemRepository.save(itemUpd);
    }

    @Override
    @Transactional
    public Comment addComment(Integer itemId, Comment comment, Integer userId) {
        if (comment.getText().isEmpty()) {
            throw new ValidateException("Пустой комментарий");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item itemForComment = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Booking> bookingsPast = bookingRepository.findByBookerIdAndItemIdPastBookings(user.getId(), itemForComment.getId());

        List<Booking> bookingFuture = bookingRepository.findByBookerIdAndItemIdFutureBookings(user.getId(), itemForComment.getId());

        if (!bookingFuture.isEmpty() && bookingsPast.isEmpty()) {
            throw new AccessibilityErrorException("Пользователь забронировал эту вещь в будущем");
        }

        if (bookingsPast.isEmpty()) {
            throw new AccessibilityErrorException("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось");
        }

        return commentRepository.save(commentMapper.commentFromCommentRequest(commentMapper.commentRequestFromComment(comment), user, itemForComment));
    }

    public void validateItem(Item item) {
        String itemName = item.getName();
        String itemDesc = item.getDescription();
        Boolean itemAvail = item.getAvailable();

        if ((itemAvail == null) || (itemDesc == null || itemDesc.isEmpty()) || (itemName == null || itemName.isEmpty())) {
            log.info("Ошибка валидации вещи");
            throw new ValidateException("Некорректно указаны данные");
        }
    }
}
