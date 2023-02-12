package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> findAll();

    List<ItemDtoBookingsComments> findAllByUser(Integer from, Integer size, Long userId);

    List<ItemDtoBookingsComments> findAllByText(Integer from, Integer size, String text, Long userId);

    ItemDtoBookingsComments findDtoById(Long id, Long userId);

    Item findById(Long id);

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    Item updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    void deleteItem(Long itemId, Long userId);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    boolean itemExistById(Long id);
}
