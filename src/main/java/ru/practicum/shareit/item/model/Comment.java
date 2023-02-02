package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;


    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDateTime created;

    public Comment(String text) {
        this.text = text;
    }
}
