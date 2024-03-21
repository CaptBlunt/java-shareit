package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.request.dto.RequestForUser;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private RequestMapper requestMapper;

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
    void getRequestsForRequestor() {
        int userId = 1;

        User requestor = User.builder()
                .id(1)
                .build();

        Request request = Request.builder()
                .id(1)
                .description("test")
                .requestor(requestor)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        Item item = Item.builder()
                .id(1)
                .name("dasd")
                .description("dsad")
                .available(true)
                .build();

        ItemForRequest itemZ = ItemForRequest.builder()
                .id(1)
                .name("dasd")
                .description("dsad")
                .available(true)
                .requestId(1)
                .build();

        List<ItemForRequest> items = List.of(itemZ);

        List<Request> requests = List.of(request);

        RequestForUser requestT = RequestForUser.builder()
                .id(1)
                .description("test")
                .created(LocalDateTime.now().minusDays(1))
                .currentDate(LocalDateTime.now())
                .items(items)
                .build();

        List<RequestForUser> requestForUsers = List.of(requestT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));

        when(requestService.getRequests(userId)).thenReturn(requests);

        when(itemRepository.findByRequestId(request.getId())).thenReturn(item);

        when(itemMapper.itemForRequestFromItem(item)).thenReturn(itemZ);

        when(requestMapper.requestForUser(request, items)).thenReturn(requestT);

        List<RequestForUser> result = requestService.getRequestsForUser(userId);

        assertEquals(result.size(), requestForUsers.size());
        assertEquals(result.get(0), requestForUsers.get(0));
    }

    @Test
    void getAllRequestsWhenWithoutParameters() {
        int userId = 1;
        User user = User.builder()
                .id(userId)
                .build();

        User requestor = User.builder()
                .id(2)
                .build();

        Request request = Request.builder()
                .id(1)
                .description("Test")
                .requestor(requestor)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        List<Request> requests = List.of(request);
        when(requestRepository.findAllNotForCreator(userId)).thenReturn(requests);

        List<Request> result = requestService.getAllRequests(userId, null, null);

        assertEquals(result, requests);
    }

    @Test
    void getAllRequestsWhenEmptyListWithParameters() {
        int userId = 1;
        int from = 1;
        int size = 10;


        List<Request> requests = Collections.emptyList();
        PageRequest page = PageRequest.of(from / size, size);

        when(requestRepository.findAllNotForCreator(eq(userId), eq(page))).thenReturn(requests);

        List<Request> result = requestService.getAllRequests(userId, from, size);

        assertEquals(result, requests);
    }

    @Test
    void getAllRequestsWhenRequestsExistsWithParameters() {
        int userId = 1;
        int from = 1;
        int size = 10;

        User requestor = User.builder()
                .id(1)
                .build();

        Request request = Request.builder()
                .id(1)
                .description("Test")
                .requestor(requestor)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        Item item = Item.builder()
                .id(1)
                .name("dasd")
                .description("dsad")
                .available(true)
                .request(request)
                .build();

        ItemForRequest request1 = ItemForRequest.builder()
                .id(1)
                .name("dasd")
                .description("dasd")
                .available(true)
                .requestId(1)
                .build();

        List<ItemForRequest> itemForRequest = Collections.singletonList(request1);
        List<Request> requests = Collections.singletonList(request);

        PageRequest page = PageRequest.of(from / size, size);
        RequestForUser requestForUser = RequestForUser.builder()
                .id(1)
                .description("dasd")
                .items(itemForRequest)
                .build();

        List<RequestForUser> requestForUsers = Collections.singletonList(requestForUser);

        when(requestRepository.findAllNotForCreator(eq(userId), eq(page))).thenReturn(requests);

        when(itemRepository.findByRequestId(request.getId())).thenReturn(item);

        when(itemMapper.itemForRequestFromItem(item)).thenReturn(request1);

        when(requestMapper.requestForUser(request, itemForRequest)).thenReturn(requestForUser);

        List<RequestForUser> result = requestService.getAllRequestForUser(userId, from, size);

        assertEquals(result.get(0).getId(), requestForUsers.get(0).getId());
    }

    @Test
    void paginationNotValid() {
        ValidateException exception = assertThrows(ValidateException.class, () -> requestService.getAllRequests(1, -1, 10));
        assertEquals("Проверьте указанные параметры", exception.getMessage());
    }

    @Test
    void getRequestByIdForUser() {
        int requestId = 1;
        int userId = 1;

        User user = User.builder()
                .id(userId)
                .build();

        User requestor = User.builder()
                .id(2)
                .build();

        Request request = Request.builder()
                .id(requestId)
                .description("Test")
                .requestor(requestor)
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        Item item = Item.builder()
                .id(1)
                .name("dasd")
                .description("dsad")
                .available(true)
                .build();

        ItemForRequest itemForRequest = ItemForRequest.builder()
                .id(1)
                .name("dasd")
                .description("dsad")
                .available(true)
                .requestId(requestId).build();

        RequestForUser requestResp = RequestForUser.builder()
                .id(requestId)
                .description("Test")
                .created(LocalDateTime.now().minusDays(1))
                .currentDate(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        List<ItemForRequest> requestList = List.of(itemForRequest);

        when(userRepository.findById(requestId)).thenReturn(Optional.of(user));

        when(itemRepository.findByRequestId(requestId)).thenReturn(item);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        when(itemMapper.itemForRequestFromItem(item)).thenReturn(itemForRequest);

        when(requestMapper.requestForUser(request, requestList)).thenReturn(requestResp);

        RequestForUser result = requestService.getRequestByIdForUser(requestId, userId);

        assertEquals(result.getId(), request.getId());
        assertEquals(result.getDescription(), request.getDescription());
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