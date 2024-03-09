package ru.practicum.shareit.user.storage;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface UserServiceDao {
    @Transactional
    UserDto createUser(User user);

    @Transactional(readOnly = true)
    List<UserDto> getAllUsers();

    @Transactional(readOnly = true)
    UserDto getUserById(Integer id);

    @Transactional
    UserDto updateUser(Integer id, User user);

    @Transactional
    void deleteUser(Integer id);
}
