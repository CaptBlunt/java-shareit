package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createValidUserTest() {
        User user = User.builder()
                .email("bob@gamail.com")
                .name("Bob")
                .build();

        User savedUser = User.builder()
                .id(1)
                .email("bob@gamail.com")
                .name("Bob")
                .build();
        when(userRepository.save(user)).thenReturn(savedUser);

        User user1 = userService.createUser(user);
        verify(userRepository).save(user);
        assertNotNull(user1);
        assertEquals(user1.getId(), savedUser.getId());
    }

    @Test
    void createNotValidEmailUserTest() {
        User user = new User(null, "", "Bob");

        ValidateException exception = assertThrows(ValidateException.class, () -> {
            userService.createUser(user);
        });
        assertEquals("Ошибка валидации. Поле Email не может быть пустым!", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void createNotValidNameUserTest() {
        User user = new User(null, "bob@gmail.com", "");
        ValidateException exception = assertThrows(ValidateException.class, () -> {
            userService.createUser(user);
        });
        assertEquals("Ошибка валидации. Поле Имя не может быть пустым!", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    void getAllUsers() {
        User user = new User(1, "bob@gamail.com", "Bob");
        User user2 = new User(2, "bob2@gamail.com", "Bob2");

        List<User> usersAll = new ArrayList<>(Arrays.asList(user, user2));
        when(userRepository.findAll()).thenReturn(usersAll);

        List<User> result = userService.getAllUsers();
        assertNotNull(result);
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
    }

    @Test
    void getUserById() {
        User user = new User(1, "bob@gamail.com", "Bob");

        Integer id = 1;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);
        assertEquals(user, result);
        verify(userRepository).findById(id);
    }

    @Test
    void getUserByIdNotFound() {
        Integer id = 0;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(id));

        assertEquals("Пользователь не найден", exception.getMessage());

    }

    @Test
    void updateUser() {

        Integer id = 1;

        User userOld = new User();
        userOld.setEmail("bob@gamail.com");
        userOld.setName("Bob");

        User userNew = new User();
        userNew.setEmail("bob2@gamail.com");
        userNew.setName("Bob2");

        when(userRepository.findById(id)).thenReturn(Optional.of(userOld));

        userService.updateUser(id, userNew);

        verify(userRepository).save(userArgumentCaptor.capture());

        User savedUSer = userArgumentCaptor.getValue();

        assertEquals("bob2@gamail.com", savedUSer.getEmail());
        assertEquals("Bob2", savedUSer.getName());
    }

    @Test
    void deleteUserTest() {
        User user = new User(1, "bob@gamail.com", "Bob");

        userService.deleteUser(user.getId());

        verify(userRepository).deleteById(user.getId());

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
