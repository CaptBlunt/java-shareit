package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    private final UserMapper userMapper;


    public List<UserDto> getAllUsers() {
        log.info("Отправлен список всех пользователей {}", inMemoryUserStorage.getAllUsers());
        return inMemoryUserStorage.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(User user) {
        return userMapper.toUserDto(inMemoryUserStorage.createUser(user));
    }

    public UserDto getUserById(Integer id) {
        return userMapper.toUserDto(inMemoryUserStorage.getUserById(id));
    }

    public UserDto updateUser(Integer id, User user) {
        return userMapper.toUserDto(inMemoryUserStorage.updateUser(id, user));
    }

    public void deleteUser(Integer id) {
        inMemoryUserStorage.deleteUser(id);
    }
}
