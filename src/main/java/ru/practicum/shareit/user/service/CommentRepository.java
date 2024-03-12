package ru.practicum.shareit.user.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comments.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select * from comments " +
            "left join items as it on it.id = comments.item_id " +
            "where it.id = ?1", nativeQuery = true)
    List<Comment> findByItemIdAndOwnerId(Integer itemId);
}
