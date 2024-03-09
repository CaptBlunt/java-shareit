package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Email(message = "Некорректный email")
    @NotNull(message = "Поле не может быть null")
    @Column(nullable = false, length = 40)
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым")
    @NotNull(message = "Поле имени не может быть null")
    @Column(nullable = false, length = 20)
    private String name;
}