package ru.practicum.shareit.user.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Пришёл GET запрос /users");
        List<UserDto> response = userService.getAllUsers();
        log.info("Отправлен ответ getAllUsers /users с телом {}", response);
        return response;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        log.info("Пришёл GET запрос /users/{}", id);
        UserDto response = userService.getUserById(id);
        log.info("Отправлен ответ getUserById /users/{} с телом {}", id, response);
        return response;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Пришёл POST запрос /users с телом {}", user);
        UserDto response = userService.createUser(user);
        log.info("Отправлен ответ createUser /users с телом {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Integer id, @RequestBody User user) {
        log.info("Пришёл PATCH запрос /users/{} с телом {}", id, user);
        UserDto response = userService.updateUser(id, user);
        log.info("Отправлен ответ updateUser /users/{} с телом {}", id, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Пришёл DELETE запрос /users/{}", id);
        userService.deleteUser(id);
    }
}
