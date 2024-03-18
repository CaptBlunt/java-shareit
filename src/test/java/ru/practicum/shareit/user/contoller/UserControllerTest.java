package ru.practicum.shareit.user.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllUsersEmptyList() throws Exception {
        List<User> emptyList = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(emptyList);

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(emptyList)));
    }

    @Test
    void getAllUsersWhenThreeUsers() throws Exception {
        User user = new User();
        User user2 = new User();
        User user3 = new User();
        List<User> users = Arrays.asList(user, user2, user3);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void getUserById() throws Exception {
        Integer id = 1;
        User user = new User();

        when(userService.getUserById(id)).thenReturn(user);

        mockMvc.perform(
                        get("/users/{id}", id)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void createUser() throws Exception {
        User user = new User(1, "bob@gamail.com", "Bob");

        when(userService.createUser(Mockito.any())).thenReturn(user);

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void updateUser() throws Exception {

        Integer id = 1;

        User user = new User();
        user.setId(id);
        user.setEmail("bobUpd@gamail.com");
        user.setName("BobUpd");

        User user2 = new User();
        user2.setEmail("bobUpd@gamail.com");
        user2.setName("BobUpd");

        when(userService.updateUser(id, user2)).thenReturn(user);

        mockMvc.perform(
                        patch("/users/{id}", user.getId())
                                .content(objectMapper.writeValueAsString(user2))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.email").value("bobUpd@gamail.com"))
                .andExpect(jsonPath("$.name").value("BobUpd"));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(
                        delete("/users/{id}", 1))
                .andExpect(status().isOk());
    }
}