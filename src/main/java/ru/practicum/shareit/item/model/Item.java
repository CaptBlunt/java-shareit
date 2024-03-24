package ru.practicum.shareit.item.model;

import lombok.*;
import net.bytebuddy.utility.nullability.MaybeNull;
import ru.practicum.shareit.comments.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    private Boolean available;

    @Transient
    private ItemResponse.ItemForOwner lastBooking;

    @Transient
    private ItemResponse.ItemForOwner nextBooking;

    @Transient
    private List<CommentResponse> comments = new ArrayList<>();

    @ManyToOne
    @MaybeNull
    @JoinColumn(name = "request_id")
    private Request request;

    public Item(Integer id, String name, String description, Boolean available, List<CommentResponse> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = comments;
    }

    public Item(Integer id, String name, String description, Boolean available, List<CommentResponse> comments, Request request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = comments;
        this.request = request;
    }

    public Item(String name, String description, User owner, boolean available) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }

    public Item(int id, String name, String description, User owner, CommentResponse commentResponse, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }

    public Item(String name, String description, User owner, boolean available, Request request) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
        this.request = request;
    }

    public Item(int id, String name, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item(int id, String name, String description, User owner, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.available = available;
    }
}