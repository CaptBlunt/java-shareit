package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationUserException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();

    private Integer generatorId = 0;


    @Override
    public User createUser(User user) {

        final String email = user.getEmail();
        if (emailUniqSet.contains(email)) {
            throw new ValidationUserException(String.format("Пользователь с эл. адресом %s уже зарегистрирован", email));
        }
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);
        emailUniqSet.add(email);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!users.containsKey(id)) {
            log.info("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
        User searchedUser = users.get(id);
        log.info("Отправлен пользователь с id {}", searchedUser);
        return searchedUser;
    }

    @Override
    public User updateUser(Integer id, User user) {
        User updatableUser = getUserById(id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(updatableUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(updatableUser.getEmail());
        } else if (!user.getEmail().equals(updatableUser.getEmail())) {
            if (emailUniqSet.contains(user.getEmail())) {
                throw new ValidationUserException(String.format("Пользователь с эл. адресом %s уже зарегистрирован", user.getEmail()));
            }
        }
        emailUniqSet.remove(updatableUser.getEmail());
        users.put(id, user);
        log.info("Данные пользователя {} изменились {}", updatableUser, getUserById(id));
        emailUniqSet.add(user.getEmail());
        return getUserById(id);
    }

    @Override
    public void deleteUser(Integer id) {
        User delUser = getUserById(id);
        emailUniqSet.remove(getUserById(id).getEmail());
        users.remove(id);
        log.info("Пользователь {} удалён", delUser);
    }
}

