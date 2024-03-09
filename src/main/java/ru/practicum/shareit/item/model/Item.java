package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.comments.model.dto.CommentDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
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

    @Column(name = "owner_id")
    private Integer ownerId;

    private Boolean available;

    @Transient
    private List<CommentDto> comments = new ArrayList<>();

    @Column(name = "request_id")
    private Integer requestId;
}