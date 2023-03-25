package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemMapper.toListItemDto(itemRepository.findAllByRequestId(itemRequest.getId()))
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getDescription()
        );
    }

    public List<ItemRequestDto> toListRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> dtos = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            dtos.add(toRequestDto(request));
        }
        return dtos;
    }
}
