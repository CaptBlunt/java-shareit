package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {

    Request createRequest(Request request);

    List<Request> getRequests(Integer userId);

    List<Request> getAllRequests(Integer userId, Integer from, Integer size);

    Request getRequestById(Integer requestId);
}
