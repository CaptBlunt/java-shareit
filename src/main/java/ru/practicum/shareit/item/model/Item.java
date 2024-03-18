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

    @NotBlank(message = "Поле названия не может быть пустым")
    @NotNull(message = "Поле названия не может быть null")
    @Column(nullable = false, length = 20)
    private String name;

    @NotBlank(message = "Поле описания не может быть пустым")
    @NotNull(message = "Поле описания не может быть null")
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

    @OneToOne
    @MaybeNull
    @JoinColumn(name = "request_id")
    private Request request;
}