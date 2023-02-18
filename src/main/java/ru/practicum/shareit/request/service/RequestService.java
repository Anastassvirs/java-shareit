package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest create(ItemRequestDto itemRequest, Long userId);

    List<ItemRequestDto> findAllByOwnerWithResponses(Long userId);

    List<ItemRequestDto> findAll(Integer from, Integer size, Long userId);

    ItemRequestDto findByIdWithResponses(Long requestId, Long userId);
}
