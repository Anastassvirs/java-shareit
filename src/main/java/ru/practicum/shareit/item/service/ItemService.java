package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {

    List<Item> findAll();

    List<Item> findAllByUser(Long userId);

    List<Item> findAllByText(String text);

    Item findById(Long id);

    Item createItem(ItemDto itemDto, User owner);

    Item updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    void deleteItem(Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
