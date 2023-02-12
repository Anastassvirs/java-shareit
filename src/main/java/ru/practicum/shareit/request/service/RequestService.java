package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest create(RequestDto itemRequest, Long userId);

    List<RequestDto> findAllByOwnerWithResponses(Long userId);

    List<ItemRequest> findAll(Integer from, Integer size, Long userId);

    RequestDto findByIdWithResponses(Long requestId, Long userId);
}
