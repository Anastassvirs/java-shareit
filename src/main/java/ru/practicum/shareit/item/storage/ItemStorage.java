package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> findAll();

    Item findById(Long id);

    Item saveItem(Item user);

    Item updateItem(Item user);

    Item deleteItem(Item user);
}
