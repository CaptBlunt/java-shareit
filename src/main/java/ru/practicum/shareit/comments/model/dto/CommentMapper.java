package ru.practicum.shareit.comments.model.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    private final UserRepository userRepository;

    public CommentDto commentDtoFromComment(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());

        User user = userRepository.getReferenceById(comment.getAuthorName());

        dto.setAuthorName(user.getName());
        dto.setCreated(comment.getCreated());

        return dto;
    }

    public List<CommentDto> comments(List<Comment> commentsList) {
        return commentsList.stream()
                .map(this::commentDtoFromComment)
                .collect(Collectors.toList());
    }
}
