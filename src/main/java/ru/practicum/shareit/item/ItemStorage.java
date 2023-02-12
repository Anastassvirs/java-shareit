package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> findAll();

    List<Item> findAllByUser(Long userId);

    List<Item> findAllByText(String text);

    Item findById(Long id);

    Item saveItem(Item user);

    Item updateItem(Item user);

    void deleteItem(Long userId);
}
