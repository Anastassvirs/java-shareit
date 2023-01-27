package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {

    List<Item> findAll();

    List<ItemDtoBookingsComments> findAllByUser(Long userId);

    List<ItemDtoBookingsComments> findAllByText(String text);

    ItemDtoBookingsComments findDtoById(Long id);

    Item findById(Long id);

    Item createItem(ItemDto itemDto, User owner);

    Item updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    void deleteItem(Long itemId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
