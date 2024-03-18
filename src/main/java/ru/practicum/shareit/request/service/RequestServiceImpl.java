package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.dto.RequestForUser;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.service.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;
    private final RequestMapper requestMapper;

    public PageRequest pagination(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }

    @Override
    public Request createRequest(Request request) {
        userRepository.findById(request.getRequestor().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new ValidateException("Запрос не может быть пустым");
        }

        Request saveRequest = requestRepository.save(request);
        return requestRepository.getReferenceById(saveRequest.getId());
    }

    public List<Request> getRequests(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Request> requests = requestRepository.findByRequestorIdOrderByCreatedDateDesc(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests;
    }

    public List<RequestForUser> getRequestsForUser(Integer userId) {

        List<Request> requests = getRequests(userId);
        List<RequestForUser> requestForUsers = new ArrayList<>();

        for (Request request : requests) {
            List<ItemForRequest> itemForRequest = new ArrayList<>();
            if (itemRepository.findByRequestId(request.getId()) == null) {
                itemForRequest = Collections.emptyList();
            } else {
                itemForRequest.add(itemMapper.itemForRequestFromItem(itemRepository.findByRequestId(request.getId())));
            }
            requestForUsers.add(requestMapper.requestForUser(request, itemForRequest));
        }
        return requestForUsers;
    }

    @Override
    public List<Request> getAllRequests(Integer userId, Integer from, Integer size) {
        List<Request> requests;
        if (from == null && size == null) {
            requests = requestRepository.findAllNotForCreator(userId);
        } else {

            if (from < 0 || size < 0) {
                throw new ValidateException("Проверьте указанные параметры");
            }
            PageRequest pageable = pagination(from, size);
            requests = requestRepository.findAllNotForCreator(userId, pageable);
            if (requests.isEmpty()) {
                return Collections.emptyList();
            }
        }
        return requests;
    }

    public List<RequestForUser> getAllRequestForUser(Integer userId, Integer from, Integer size) {
        List<Request> requests = getAllRequests(userId, from, size);

        List<RequestForUser> requestForUsers = new ArrayList<>();

        for (Request request : requests) {
            List<ItemForRequest> itemForRequest = new ArrayList<>();
            if (itemRepository.findByRequestId(request.getId()) == null) {
                itemForRequest = Collections.emptyList();
            } else {
                itemForRequest.add(itemMapper.itemForRequestFromItem(itemRepository.findByRequestId(request.getId())));
            }
            requestForUsers.add(requestMapper.requestForUser(request, itemForRequest));
        }
        return requestForUsers;
    }

    @Override
    public Request getRequestById(Integer requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }

    public RequestForUser getRequestByIdForUser(Integer requestId, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemForRequest> itemForRequest = new ArrayList<>();
        if (itemRepository.findByRequestId(requestId) == null) {
            itemForRequest = Collections.emptyList();
        } else {
            itemForRequest.add(itemMapper.itemForRequestFromItem(itemRepository.findByRequestId(requestId)));
        }
        return requestMapper.requestForUser(getRequestById(requestId), itemForRequest);
    }
}
