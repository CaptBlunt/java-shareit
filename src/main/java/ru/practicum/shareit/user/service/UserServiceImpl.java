package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        String newName = user.getName();
        String newEmail = user.getEmail();
        if (!isNotEmpty(newName) || !isNotEmpty(newEmail)) {
            throw new ValidateException("Ошибка валидации. Поле " +
                    (newName.isEmpty() ? "Имя" : "Email") + " не может быть пустым!");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Transactional
    @Override
    public User updateUser(Integer id, User user) {
        User userUpd = getUserById(id);
        if (user.getEmail() != null) {
            userUpd.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userUpd.setName(user.getName());
        }
        return userRepository.save(userUpd);
    }

    @Transactional
    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
