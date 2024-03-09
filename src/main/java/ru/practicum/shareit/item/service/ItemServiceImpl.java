package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.model.dto.CommentDto;
import ru.practicum.shareit.comments.model.dto.CommentMapper;
import ru.practicum.shareit.comments.storage.CommentRepository;
import ru.practicum.shareit.exception.AccessibilityErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemServiceDao;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemServiceDao {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(Item item, Integer userId) {
        validateItem(item);
        userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setOwnerId(userId);
        Item it = itemRepository.save(item);
        return itemMapper.toItemDto(it);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Integer id, Integer userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwnerId().equals(userId)) {
            return itemMapper.toItemDto(item);
        } else {
            return itemMapper.toItemDtoForOwner(item);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findByOwnerId(Integer userId) {
        return itemMapper.toItemsDtoOwner(itemRepository.findByOwnerId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchBySubstring(String str, String str1) {
        if (str.isEmpty()) {
            return new ArrayList<>();
        }
        return itemMapper.toItemsDto(itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase(str, str1)).stream()
                .filter(itemDto -> itemDto.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Integer id, Item item, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item itemUpd = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!userId.equals(itemUpd.getOwnerId())) {
            throw new NotFoundException("Пользователь " + userId + " не является владельцем  вещи " + id);
        }
        String newName = item.getName();
        String newDesc = item.getDescription();
        Boolean newAva = item.getAvailable();

        if (newName == null) {
            item.setName(itemUpd.getName());
        }
        if (newDesc == null) {
            item.setDescription(itemUpd.getDescription());
        }
        if (newAva == null) {
            item.setAvailable(itemUpd.getAvailable());
        }

        itemUpd.setName(item.getName());
        itemUpd.setDescription(item.getDescription());
        itemUpd.setAvailable(item.getAvailable());

        return itemMapper.toItemDto(itemRepository.save(itemUpd));
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer itemId, CommentDto.CommentDtoPost comment, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item itemForComment = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Booking> bookingsPast = bookingRepository.findByBookerIdAndItemIdPastBookings(userId, itemId);

        if (bookingsPast.isEmpty()) {
            throw new AccessibilityErrorException("Пользователь не бронировал эту вещь, либо бронирование ещё не закончилось");
        }

        List<Booking> bookingFuture = bookingRepository.findByBookerIdAndItemIdFutureBookings(userId, itemId);

        if (!bookingFuture.isEmpty() && bookingsPast.isEmpty()) {
            throw new AccessibilityErrorException("Пользователь забронировал эту вещь в будущем");
        }

        Comment newComment = new Comment();

        if (comment.getText().isEmpty()) {
            throw new ValidateException("Пустой комментарий");
        }
        newComment.setText(comment.getText());
        newComment.setItem(itemId);
        newComment.setAuthorName(userId);
        newComment.setCreated(LocalDateTime.now());

        Comment commentSave = commentRepository.save(newComment);

        List<CommentDto> comments = itemForComment.getComments();

        CommentDto commentDto = commentMapper.commentDtoFromComment(commentSave);

        comments.add(commentDto);

        itemForComment.setComments(comments);

        return commentDto;
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
