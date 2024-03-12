package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    public ItemResponse itemResponseFromItemForUser(Item item, List<CommentResponse> comments) {
        ItemResponse dto = new ItemResponse();

        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(comments);
        return dto;
    }

    public ItemResponse itemResponseFromItem(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setAvailable(item.getAvailable());
        response.setLastBooking(item.getLastBooking());
        response.setNextBooking(item.getNextBooking());
        response.setComments(item.getComments());
        return response;
    }

    public Item itemFromItemResponse(ItemResponse itemResponse) {
        Item item = new Item();
        item.setId(itemResponse.getId());
        item.setName(itemResponse.getName());
        item.setDescription(itemResponse.getDescription());
        item.setAvailable(itemResponse.getAvailable());
        item.setLastBooking(itemResponse.getLastBooking());
        item.setNextBooking(itemResponse.getNextBooking());
        item.setComments(itemResponse.getComments());
        return item;
    }

    public Item itemFromItemRequest(ItemRequest itemRequest, Integer userId) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setAvailable(itemRequest.getAvailable());
        item.setOwnerId(userId);
        return item;
    }

    public ItemResponse itemForCreate(Item item) {
        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(new ArrayList<>());
        return dto;
    }

    public ItemResponse itemForOwner(Item item, List<CommentResponse> comments, List<Booking> bookings, List<Booking> pastBookings, List<Booking> futureBookings) {
        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (bookings.isEmpty()) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return dto;

        } else {
            ItemResponse.ItemForOwner lastBookingDto = null;
            if (!pastBookings.isEmpty()) {
                Booking lastBooking = pastBookings.get(0);
                lastBookingDto = new ItemResponse.ItemForOwner();
                lastBookingDto.setId(lastBooking.getId());
                lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            }
            dto.setLastBooking(lastBookingDto);

            ItemResponse.ItemForOwner nextBookingDto = null;
            if (!futureBookings.isEmpty()) {
                Booking nextBooking = futureBookings.get(futureBookings.size() - 1);
                nextBookingDto = new ItemResponse.ItemForOwner();
                nextBookingDto.setId(nextBooking.getId());
                nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            }
            dto.setNextBooking(nextBookingDto);
            dto.setComments(comments);
        }
        return dto;
    }

    public List<ItemResponse> itemsForResponse(List<Item> items) {
        return items.stream()
                .map(this::itemResponseFromItem)
                .collect(Collectors.toList());
    }

    public Item itemFromUpdate(ItemUpdateRequest itemUpdate, Integer userId, Integer id) {
        Item item = new Item();
        item.setId(id);
        item.setOwnerId(userId);
        item.setName(itemUpdate.getName());
        item.setDescription(itemUpdate.getDescription());
        item.setAvailable(itemUpdate.getAvailable());

        return item;
    }
}