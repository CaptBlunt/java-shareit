package ru.practicum.shareit.comments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "comments")
@AllArgsConstructor
@RequiredArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User authorName;

    @Column(name = "created_date")
    private LocalDateTime created;

    public Comment(String text, Item item, LocalDateTime created, User authorName) {
        this.text = text;
        this.item = item;
        this.created = created;
        this.authorName = authorName;
    }

    public Comment(int id, String text, User authorName, LocalDateTime created) {
        this.id = id;
        this.text = text;
        this.created = created;
        this.authorName = authorName;
    }
}
