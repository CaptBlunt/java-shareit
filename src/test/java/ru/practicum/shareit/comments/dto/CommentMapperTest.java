package ru.practicum.shareit.comments.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
    }

    CommentRequest commentRequest = new CommentRequest("test");
    User author = new User(1, "abc@de.com", "test");

    Item item = new Item();

    Comment comment = new Comment(1, "test", author, LocalDateTime.now());

    @Test
    void commentFromCommentRequest() {
        Comment comment = commentMapper.commentFromCommentRequest(commentRequest, author, item);

        assertEquals(comment.getText(), commentRequest.getText());
        assertEquals(comment.getAuthorName(), author);
        assertEquals(comment.getItem(), item);
    }

    @Test
    void commentResponseFromComment() {
        CommentResponse commentResponse = commentMapper.commentResponseFromComment(comment, "name");

        assertEquals(comment.getText(), commentResponse.getText());
        assertEquals(comment.getCreated(), commentResponse.getCreated());
    }

    @Test
    void commentResponse() {
        CommentResponse commentResponse = commentMapper.commentResponse(comment);

        assertEquals(commentResponse.getText(), comment.getText());
        assertEquals(commentResponse.getAuthorName(), comment.getAuthorName().getName());
        assertEquals(commentResponse.getCreated(), comment.getCreated());
    }
}