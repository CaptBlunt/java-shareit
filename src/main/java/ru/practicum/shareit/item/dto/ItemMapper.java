package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {

    public ItemResponse itemResponseFromItemForUser(Item item, List<CommentResponse> comments) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(comments).build();
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
        if (item.getRequest() != null) {
            response.setRequestId(item.getRequest().getId());
        }
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
        User user = new User();
        user.setId(userId);
        item.setOwner(user);
        if (!(itemRequest.getRequestId() == null)) {
            Request request = new Request();
            request.setId(itemRequest.getRequestId());
            item.setRequest(request);
        }
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
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public ItemResponse itemForOwner(Item item, List<CommentResponse> comments, List<Booking> bookings, List<Booking> pastBookings, List<Booking> futureBookings) {
        ItemResponse dto = new ItemResponse();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setComments(comments);

        if (bookings.isEmpty()) {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
            return dto;
        }

        if (!pastBookings.isEmpty()) {
            Booking lastBooking = pastBookings.get(0);
            ItemResponse.ItemForOwner lastBookingDto = new ItemResponse.ItemForOwner();
            lastBookingDto.setId(lastBooking.getId());
            lastBookingDto.setBookerId(lastBooking.getBooker().getId());
            dto.setLastBooking(lastBookingDto);
        }

        if (!futureBookings.isEmpty()) {
            Booking nextBooking = futureBookings.get(futureBookings.size() - 1);
            ItemResponse.ItemForOwner nextBookingDto = new ItemResponse.ItemForOwner();
            nextBookingDto.setId(nextBooking.getId());
            nextBookingDto.setBookerId(nextBooking.getBooker().getId());
            dto.setNextBooking(nextBookingDto);
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
        User user = new User();
        user.setId(userId);
        item.setOwner(user);
        item.setName(itemUpdate.getName());
        item.setDescription(itemUpdate.getDescription());
        item.setAvailable(itemUpdate.getAvailable());

        return item;
    }

    public ItemForRequest itemForRequestFromItem(Item item) {
        ItemForRequest itemForRequest = new ItemForRequest();
        itemForRequest.setId(item.getId());
        itemForRequest.setName(item.getName());
        itemForRequest.setDescription(item.getDescription());
        itemForRequest.setAvailable(item.getAvailable());
        itemForRequest.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemForRequest;
    }
}