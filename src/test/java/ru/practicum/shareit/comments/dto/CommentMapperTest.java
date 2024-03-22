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

    CommentRequest commentRequest = CommentRequest.builder()
            .text("test")
            .build();

    User author = User.builder()
            .id(1)
            .email("abc@de.com")
            .name("test")
            .build();

    Item item = Item.builder()
            .id(1)
            .build();

    Comment comment = Comment.builder()
            .id(1)
            .text("test")
            .authorName(author)
            .created(LocalDateTime.now())
            .build();

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