package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.storage.UserServiceDao;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserServiceDao {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(User user1) {
        String newName = user1.getName();
        String newEmail = user1.getEmail();
        if (!isNotEmpty(newName) || !isNotEmpty(newEmail)) {
            throw new ValidateException("Ошибка валидации. Поле " +
                    (newName.isEmpty() ? "Email" : "Имя") + " не может быть пустым!");
        }
        User user2 = userRepository.save(user1);
        return userMapper.toUserDto(user2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUsersDto(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Integer id, User user) {
        User userUpd = userRepository.getReferenceById(id);

        String newName = user.getName();
        String newEmail = user.getEmail();

        if (newName == null) {
            user.setName(userUpd.getName());
        }
        if (newEmail == null) {
            user.setEmail(userUpd.getEmail());
        }
        userUpd.setName(user.getName());
        userUpd.setEmail(user.getEmail());
        return userMapper.toUserDto(userRepository.save(userUpd));
    }

    @Transactional
    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
