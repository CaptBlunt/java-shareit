package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationUserException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public List<UserDto> getAllUsers() {
        log.info("Отправлен список всех пользователей {}", inMemoryUserStorage.getAllUsers());
        return inMemoryUserStorage.getAllUsers();
    }

    public UserDto createUser(User user) {
        validateUser(user);
        return inMemoryUserStorage.createUser(user);
    }

    public UserDto getUserById(Integer id) {
        return inMemoryUserStorage.getUserById(id);
    }

    public UserDto updateUser(Integer id, User user) {
        if (!getUserById(id).getEmail().equals(user.getEmail())) {
            validateUser(user);
        }
        return inMemoryUserStorage.updateUser(id, user);
    }

    public void deleteUser(Integer id) {
        inMemoryUserStorage.deleteUser(id);
    }

    public List<String> getAllEmails() {
        List<UserDto> allUsers = inMemoryUserStorage.getAllUsers();
        List<String> allEmails = new ArrayList<>();
        for (UserDto user : allUsers) {
            allEmails.add(user.getEmail());
        }
        return allEmails;
    }

    public void validateUser(User user) {
        String userEmail = user.getEmail();

        if (getAllEmails().contains(userEmail)) {
            log.info("Ошибка валидации пользователя {}", user);
            throw new ValidationUserException(String.format("Пользователь с эл. адресом %s уже зарегистрирован", userEmail));
        }
    }
}
