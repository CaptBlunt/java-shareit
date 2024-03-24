package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDateTime createdDate;

    public Request(String description, LocalDateTime createdDate) {
        this.description = description;
        this.createdDate = createdDate;
    }

    public Request(int id, String description, LocalDateTime createdDate) {
        this.id = id;
        this.description = description;
        this.createdDate = createdDate;
    }

    public Request(String description, User requestor, LocalDateTime createdDate) {
        this.description = description;
        this.requestor = requestor;
        this.createdDate = createdDate;
    }
}
