package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.UsersItemRequestResponse;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestResponse;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestServiceImpl requestService;
    private final ItemRequestMapper itemRequestMapper;

    @PostMapping
    public CreateItemRequestResponse postRequest(@RequestBody @Valid CreateItemRequest request, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /requests от пользователя id {} с телом {}", userId, request);
        Request response = requestService.createRequest(itemRequestMapper.requestForCreate(request, userId));
        log.info("Отправлен ответ postRequest /requests с телом {}", response);
        return itemRequestMapper.requestResponseFromRequest(response);
    }

    @GetMapping
    public List<UsersItemRequestResponse> getUsersItemRequestResponse(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /requests от пользователя id {}", userId);
        List<UsersItemRequestResponse> response = requestService.getRequestsForUser(userId);
        log.info("Отправлен ответ postRequest /requests с телом {}", response);
        return response;
    }

    @GetMapping("/all")
    public List<UsersItemRequestResponse> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Integer userId, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Пришёл GET запрос /requests/all от пользователя id {}", userId);
        List<UsersItemRequestResponse> response = requestService.getAllRequestForUser(userId, from, size);
        log.info("Отправлен ответ getAllRequests /requests/all с телом {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public UsersItemRequestResponse getRequest(@PathVariable Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /requests/{} от пользователя id {}", id, userId);
        UsersItemRequestResponse response = requestService.getRequestByIdForUser(id, userId);
        log.info("Отправлен ответ getRequest /requests/{} с телом {}", id, response);
        return response;
    }
}
