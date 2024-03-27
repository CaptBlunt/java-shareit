package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.UsersItemRequestResponse;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.CreateItemRequestResponse;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestServiceImpl requestService;

    @Test
    void postRequest() throws Exception {
        Integer userId = 2;

        CreateItemRequest requestInController = new CreateItemRequest("Test");

        Request requestServiceIn = new Request("Test", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));

        Request requestServiceOut = new Request(1, "Test", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));

        CreateItemRequestResponse requestControllerOut = new CreateItemRequestResponse(1, "Test", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));


        when(requestService.createRequest(requestServiceIn)).thenReturn(requestServiceOut);

        mockMvc.perform(post("/requests", requestInController)
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .content(objectMapper.writeValueAsString(requestInController))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(requestControllerOut.getId())))
                .andExpect(jsonPath("$.description", is(requestControllerOut.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(requestControllerOut.getCreated()))));
    }

    @Test
    void getAllRequests() throws Exception {
        Integer userId = 1;
        Integer from = 0;
        Integer size = 10;

        List<UsersItemRequestResponse> requests = new ArrayList<>();
        UsersItemRequestResponse requestOne = new UsersItemRequestResponse(1, "Test", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS));
        requests.add(requestOne);

        UsersItemRequestResponse requestTwo = new UsersItemRequestResponse(1, "Test2", LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS));
        requests.add(requestTwo);

        when(requestService.getAllRequestForUser(userId, from, size)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestOne.getId())))
                .andExpect(jsonPath("$[0].created", is(String.valueOf(requestOne.getCreated()))))
                .andExpect(jsonPath("$[1].id", is(requestTwo.getId())))
                .andExpect(jsonPath("$[1].description", is(requestTwo.getDescription())));
    }

    @Test
    void getRequestByIdAndUserIdWhenRequestExists() throws Exception {
        int id = 1;
        int userId = 2;

        UsersItemRequestResponse forUser = new UsersItemRequestResponse(id, "Test", LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        when(requestService.getRequestByIdForUser(1, 2)).thenReturn(forUser);

        mockMvc.perform(get("/requests/{id}", id)
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id", is(forUser.getId())))
                .andExpect(jsonPath("$.description", is(forUser.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(forUser.getCreated()))))
                .andExpect(jsonPath("$.currentDate", is(String.valueOf(forUser.getCurrentDate()))));
    }
}