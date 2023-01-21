package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestStorage {
    List<ItemRequest> findAll();

    ItemRequest findById(Long id);

    ItemRequest save(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    void delete(Long id);
}
