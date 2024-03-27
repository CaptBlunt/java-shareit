package ru.practicum.shareit.user.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        log.info("Пришёл GET запрос /users");
        List<UserResponse> response = userMapper.usersResponseFromUsers(userService.getAllUsers());
        log.info("Отправлен ответ getAllUsers /users с телом {}", response);
        return userMapper.usersResponseFromUsers(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Integer id) {
        log.info("Пришёл GET запрос /users/{}", id);
        UserResponse response = userMapper.userResponseFromUser(userService.getUserById(id));
        log.info("Отправлен ответ getUserById /users/{} с телом {}", id, response);
        return response;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserRequest user) {
        log.info("Пришёл POST запрос /users с телом {}", user);
        UserResponse response = userMapper.userResponseFromUser(userService.createUser(userMapper.userFromUserRequest(user)));
        log.info("Отправлен ответ createUser /users с телом {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable Integer id, @RequestBody UserResponse user) {
        log.info("Пришёл PATCH запрос /users/{} с телом {}", id, user);
        UserResponse response = userMapper.userResponseFromUser(userService.updateUser(id, userMapper.userFromUserResponse(user)));
        log.info("Отправлен ответ updateUser /users/{} с телом {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /users/{}", id);
        userService.deleteUser(id);
    }
}
