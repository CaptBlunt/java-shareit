package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public User userFromUserRequest(UserRequest user) {
        User userRequest = new User();
        userRequest.setName(user.getName());
        userRequest.setEmail(user.getEmail());
        return userRequest;
    }

    public UserResponse userResponseFromUser(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User userFromUserResponse(UserResponse userResponse) {
        User user = new User();
        user.setId(userResponse.getId());
        user.setName(userResponse.getName());
        user.setEmail(userResponse.getEmail());
        return user;
    }

    public List<UserResponse> UsersResponseFromUsers(List<User> users) {
        return users.stream()
                .map(this::userResponseFromUser)
                .collect(Collectors.toList());
    }
}