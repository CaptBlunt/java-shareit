package ru.practicum.shareit.item.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.model.dto.CommentMapper;
import ru.practicum.shareit.comments.storage.CommentRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final BookingRepository bookingRepository;

    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
        if (comments.isEmpty()) {
            comments = new ArrayList<>();
        }

        dto.setComments(commentMapper.comments(comments));
        return dto;
    }

    public ItemDto toItemDtoForOwner(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (bookingRepository.findByItemId(item.getId()).isEmpty()) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return dto;
        } else {
            List<Booking> pastBookings = bookingRepository.findByOwnerIdAndItemIdPastBookings(item.getOwnerId(), item.getId());
            List<Booking> futureBookings = bookingRepository.findByOwnerIdAndItemIdFutureBookings(item.getOwnerId(), item.getId());

            ItemDto.ItemDtoForOwner lastBookingDto = null;
            if (!pastBookings.isEmpty()) {
                Booking lastBooking = pastBookings.get(0);
                lastBookingDto = new ItemDto.ItemDtoForOwner();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setBookerId(lastBooking.getBooker());
            }
            dto.setLastBooking(lastBookingDto);

            ItemDto.ItemDtoForOwner nextBookingDto = null;
            if (!futureBookings.isEmpty()) {
                Booking nextBooking = futureBookings.get(futureBookings.size() - 1);
                nextBookingDto = new ItemDto.ItemDtoForOwner();
                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setBookerId(nextBooking.getBooker());
            }
            dto.setNextBooking(nextBookingDto);

            List<Comment> comments = commentRepository.findByItemIdAndOwnerId(item.getId());
            if (comments.isEmpty()) {
                comments = new ArrayList<>();
            }
            dto.setComments(commentMapper.comments(comments));
        }
        return dto;
    }

    public List<ItemDto> toItemsDtoOwner(List<Item> items) {
        return items.stream()
                .map(this::toItemDtoForOwner)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public List<ItemDto> toItemsDto(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}