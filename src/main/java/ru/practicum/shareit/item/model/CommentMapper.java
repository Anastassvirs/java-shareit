package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public Comment newtoComment(@NotNull CommentDto commentDto, @NotNull Item item, @NotNull User author, LocalDateTime created) {
        return new Comment(
                commentDto.getText(),
                item,
                author,
                created
        );
    }

    public CommentDto toCommentDto(@NotNull Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
