package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestMapper {

    public Request requestForCreate(CreateItemRequest createItemRequest, Integer userId) {
        Request request = new Request();
        request.setDescription(createItemRequest.getDescription());
        User user = new User();
        user.setId(userId);
        request.setRequestor(user);
        request.setCreatedDate(LocalDateTime.now());
        return request;
    }

    public CreateItemRequestResponse requestResponseFromRequest(Request request) {
        CreateItemRequestResponse response = new CreateItemRequestResponse();
        response.setId(request.getId());
        response.setDescription(request.getDescription());
        response.setCreated(request.getCreatedDate());
        response.setCurrentDate(LocalDateTime.now());
        return response;
    }

    public UsersItemRequestResponse requestForUser(Request request, List<ItemForRequest> items) {
        UsersItemRequestResponse usersItemRequestResponse = new UsersItemRequestResponse();
        usersItemRequestResponse.setId(request.getId());
        usersItemRequestResponse.setDescription(request.getDescription());
        usersItemRequestResponse.setCreated(request.getCreatedDate());
        usersItemRequestResponse.setCurrentDate(LocalDateTime.now());
        usersItemRequestResponse.setItems(items);
        return usersItemRequestResponse;
    }
}
