package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void createRequestWhenRequestValid() {
        User user = User.builder()
                .id(1)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Request request = Request.builder()
                .description("Test")
                .requestor(user)
                .createdDate(LocalDateTime.now())
                .build();

        Request requestSaved = Request.builder()
                .id(1)
                .description("Test")
                .requestor(user)
                .createdDate(LocalDateTime.now())
                .build();

        when(requestRepository.save(request)).thenReturn(requestSaved);

        Request createdRequest = requestService.createRequest(request);

        verify(userRepository).findById(1);
        verify(requestRepository).save(request);
        assertEquals(requestSaved, createdRequest);
    }

    @Test
    void createRequestWhenUserNotFound() {
        User user = User.builder()
                .id(1)
                .build();

        when(userRepository.findById(anyInt())).thenThrow(new NotFoundException("Пользователь не найден"));

        Request request = Request.builder()
                .description("Test")
                .requestor(user)
                .createdDate(LocalDateTime.now())
                .build();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> requestService.createRequest(request));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getRequests() {
        User user = User.builder()
                .id(1)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));


        Request requestOne = Request.builder()
                .description("Test")
                .build();

        Request requestTwo = Request.builder()
                .description("Test2")
                .build();

        List<Request> requestList = List.of(requestOne, requestTwo);

        when(requestRepository.findByRequestorIdOrderByCreatedDateDesc(user.getId())).thenReturn(requestList);

        List<Request> result = requestService.getRequests(user.getId());

        assertEquals(result, requestList);

    }

    @Test
    void getRequestById() {
        int requestId = 1;
        Request requestOne = Request.builder()
                .id(requestId)
                .description("Test")
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(requestOne));

        Request result = requestService.getRequestById(requestId);

        assertEquals(result, requestOne);
    }
}