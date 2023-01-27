package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<Item> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Item> findAllByUser(Long userId) {
        return repository.findAllByOwner(userId);
    }

    @Override
    public List<Item> findAllByText(String text) {
        if (text.equals("")) {
            return new ArrayList<>();
        }
        return repository.findAllByText(text);
    }

    @Override
    public Item findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundAnythingException("Вещи с данным id не существует"));
    }

    @Override
    @Transactional
    public Item createItem(ItemDto itemDto, User owner) {
        log.debug("Пользователем с id: {} была добавлена новая вещь: {}", owner.getId(), itemDto);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        return repository.save(newItem);
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item item = findById(itemId);
        if (ownerId.equals(item.getOwner().getId())) {
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
            Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
            log.debug("Обновлена вещь: {}", item);
            return repository.save(item);
        } else {
            log.debug("Произошла ошибка: ID пользователя не соответсвует владельцу вещи");
            throw new NotFoundAnythingException("ID пользователя не соответсвует владельцу вещи");
        }

    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        repository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (commentDto.getText().equals("")) {
            throw new WrongParametersException("Поле текста комментария не  может быть пустым");
        }
        Comment comment = commentMapper.newtoComment(commentDto);
        User author = userService.findById(userId);
        comment.setItem(findById(itemId));
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }


}
