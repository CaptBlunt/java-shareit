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
public class RequestMapper {

    public Request requestForCreate(RequestFromUser requestFromUser, Integer userId) {
        Request request = new Request();
        request.setDescription(requestFromUser.getDescription());
        User user = new User();
        user.setId(userId);
        request.setRequestor(user);
        request.setCreatedDate(LocalDateTime.now());
        return request;
    }

    public RequestResponseForCreate requestResponseFromRequest(Request request) {
        RequestResponseForCreate response = new RequestResponseForCreate();
        response.setId(request.getId());
        response.setDescription(request.getDescription());
        response.setCreated(request.getCreatedDate());
        response.setCurrentDate(LocalDateTime.now());
        return response;
    }

    public RequestForUser requestForUser(Request request, List<ItemForRequest> items) {
        RequestForUser requestForUser = new RequestForUser();
        requestForUser.setId(request.getId());
        requestForUser.setDescription(request.getDescription());
        requestForUser.setCreated(request.getCreatedDate());
        requestForUser.setCurrentDate(LocalDateTime.now());
        requestForUser.setItems(items);
        return requestForUser;
    }
}
