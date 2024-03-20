package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    private void deleteAfter() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findAllWhenUserNotCreatorSortedCreatedDate() {

        User owner = new User();
        owner.setEmail("bob@gamail.com");
        owner.setName("Bob");
        userRepository.save(owner);

        User requestor = new User();
        requestor.setEmail("bob2@gamail.com");
        requestor.setName("Bob");
        userRepository.save(requestor);

        String str = "2024-03-08 12:30";
        String str1 = "2024-03-09 12:30";
        String str2 = "2024-03-10 12:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        LocalDateTime dateTime1 = LocalDateTime.parse(str1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(str2, formatter);

        Request request = Request.builder()
                .description("test")
                .createdDate(dateTime)
                .requestor(requestor)
                .build();
        requestRepository.save(request);

        Request requestTwo = Request.builder()
                .description("test")
                .createdDate(dateTime1)
                .requestor(owner)
                .build();
        requestRepository.save(requestTwo);

        Request requestThree = Request.builder()
                .description("test")
                .createdDate(dateTime2)
                .requestor(owner)
                .build();
        requestRepository.save(requestThree);

        List<Request> requestList = requestRepository.findAllNotForCreator(requestor.getId());

        assertEquals(requestList.size(), 2);
        assertEquals(requestList.get(0), requestTwo);
        assertEquals(requestList.get(1), requestThree);
    }
    @Test
    void findByRequestorIdOrderByCreatedDateDesc() {
        User owner = new User();
        owner.setEmail("bob1@gamail.com");
        owner.setName("Bob");
        userRepository.save(owner);

        User requestor = new User();
        requestor.setEmail("bob21@gamail.com");
        requestor.setName("Bob");
        userRepository.save(requestor);

        String str = "2024-03-08 12:30";
        String str1 = "2024-03-09 12:30";
        String str2 = "2024-03-10 12:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
        LocalDateTime dateTime1 = LocalDateTime.parse(str1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(str2, formatter);

        Request request = Request.builder()
                .description("test")
                .createdDate(dateTime1)
                .requestor(requestor)
                .build();
        requestRepository.save(request);

        Request requestTwo = Request.builder()
                .description("test")
                .createdDate(dateTime2)
                .requestor(owner)
                .build();
        requestRepository.save(requestTwo);

        List<Request> requestList = requestRepository.findByRequestorIdOrderByCreatedDateDesc(requestor.getId());

        assertEquals(requestList.size(), 1);
        assertEquals(requestList.get(0).getRequestor(), requestor);
    }
}