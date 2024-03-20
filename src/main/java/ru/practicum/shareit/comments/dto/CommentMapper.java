package ru.practicum.shareit.comments.dto;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentMapper {

    public Comment commentFromCommentRequest(CommentRequest commentRequest, User user, Item item) {
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setAuthorName(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public Comment commentForCreate(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        return comment;
    }

    public CommentRequest commentRequestFromComment(Comment comment) {
        CommentRequest request = new CommentRequest();
        request.setText(comment.getText());
        return request;
    }

    public CommentResponse commentResponseFromComment(Comment comment, String name) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(name);
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public CommentResponse commentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setAuthorName(comment.getAuthorName().getName());
        response.setCreated(comment.getCreated());
        return response;
    }
}
