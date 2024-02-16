package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    UserDto createUser(User user);

    List<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto updateUser(Integer id, User user);

    void deleteUser(Integer id);
}