package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestForUser;
import ru.practicum.shareit.request.dto.RequestFromUser;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.RequestResponseForCreate;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestServiceImpl requestService;
    private final RequestMapper requestMapper;

    @PostMapping
    public RequestResponseForCreate postRequest(@RequestBody RequestFromUser request, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл POST запрос /requests от пользователя id {} с телом {}", userId, request);
        Request response = requestService.createRequest(requestMapper.requestForCreate(request, userId));
        log.info("Отправлен ответ postRequest /requests с телом {}", response);
        return requestMapper.requestResponseFromRequest(response);
    }

    @GetMapping
    public List<RequestForUser> getRequestsUser(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /requests от пользователя id {}", userId);
        List<RequestForUser> response = requestService.getRequestsForUser(userId);
        log.info("Отправлен ответ postRequest /requests с телом {}", response);
        return response;
    }

    @GetMapping("/all")
    public List<RequestForUser> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") Integer userId, @RequestParam(required = false) Integer from, @RequestParam(required = false) Integer size) {
        log.info("Пришёл GET запрос /requests/all от пользователя id {}", userId);
        List<RequestForUser> response = requestService.getAllRequestForUser(userId, from, size);
        log.info("Отправлен ответ getAllRequests /requests/all с телом {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public RequestForUser getRequest(@PathVariable Integer id, @RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        log.info("Пришёл GET запрос /requests/{} от пользователя id {}", id, userId);
        RequestForUser response = requestService.getRequestByIdForUser(id, userId);
        log.info("Отправлен ответ getRequest /requests/{} с телом {}", id, response);
        return response;
    }
}
