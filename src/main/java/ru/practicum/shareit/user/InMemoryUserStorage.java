package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final static String NOT_FOUND_USER = "Пользователь не найден";

    private final UserMapper userMapper;
    private final Map<Integer, User> users = new HashMap<>();

    private Integer generatorId = 0;

    @Override
    public UserDto createUser(User user) {
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", userMapper.toUserDto(user));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User person : users.values()) {
            allUsers.add(userMapper.toUserDto(person));
        }
        return allUsers;
    }

    @Override
    public UserDto getUserById(Integer id) {
        if (!users.containsKey(id)) {
            log.info("Пользователь с id {} не найден", id);
            throw new NotFoundException(NOT_FOUND_USER);
        }
        User searchedUser = users.get(id);
        log.info("Отправлен пользователь с id {}", searchedUser);
        return userMapper.toUserDto(searchedUser);
    }

    @Override
    public UserDto updateUser(Integer id, User user) {
        UserDto updatableUser = getUserById(id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(updatableUser.getName());
        } else if (user.getEmail() == null) {
            user.setEmail(updatableUser.getEmail());
        }
        log.info("Данные пользователя {} изменились {}", updatableUser, getUserById(id));
        users.put(id, user);
        return getUserById(id);
    }

    @Override
    public void deleteUser(Integer id) {
        UserDto delUser = getUserById(id);
        users.remove(id);
        log.info("Пользователь {} удалён", delUser);
    }
}

