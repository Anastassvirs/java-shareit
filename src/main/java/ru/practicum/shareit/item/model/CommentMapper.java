package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;

@Component
public class CommentMapper {
    public Comment newtoComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getText()
        );
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}
