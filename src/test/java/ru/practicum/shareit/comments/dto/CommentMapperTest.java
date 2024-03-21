package ru.practicum.shareit.comments.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapper();
    }


    @Test
    void commentFromCommentRequest() {
        CommentRequest request = CommentRequest.builder()
                .text("dasd")
                .build();

        User user = User.builder()
                .id(1)
                .email("asd@dasd.com")
                .name("asda")
                .build();

        Item item = Item.builder()
                .id(1)
                .build();

        Comment comment = commentMapper.commentFromCommentRequest(request, user, item);

        assertEquals(comment.getText(), request.getText());
        assertEquals(comment.getAuthorName(), user);
        assertEquals(comment.getItem(), item);
}

    @Test
    void commentResponseFromComment() {
        Comment comment1 = Comment.builder()
                .id(1)
                .text("dasd")
                .created(LocalDateTime.now())
                .build();

        CommentResponse comment = commentMapper.commentResponseFromComment(comment1, "name");

        assertEquals(comment.getText(), comment1.getText());
        assertEquals(comment.getCreated(), comment1.getCreated());
    }

    @Test
    void commentResponse() {
        User user = User.builder()
                .id(1)
                .email("asd@dasd.com")
                .name("asda")
                .build();


        Comment comment1 = Comment.builder()
                .id(1)
                .text("dasd")
                .authorName(user)
                .created(LocalDateTime.now())
                .build();

        CommentResponse comment = commentMapper.commentResponse(comment1);

        assertEquals(comment.getText(), comment1.getText());
        assertEquals(comment.getAuthorName(), comment1.getAuthorName().getName());
        assertEquals(comment.getCreated(), comment1.getCreated());
    }

}