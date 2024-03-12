package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    User createUser(User user);

    @Transactional(readOnly = true)
    List<User> getAllUsers();

    @Transactional(readOnly = true)
    User getUserById(Integer id);

    @Transactional
    User updateUser(Integer id, User user);

    @Transactional
    void deleteUser(Integer id);
}
