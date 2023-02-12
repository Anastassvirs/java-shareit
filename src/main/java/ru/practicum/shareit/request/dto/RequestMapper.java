package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@Component
public class RequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public RequestDto toRequestDto(@NotNull ItemRequest itemRequest) {
        Long id = itemRequest.getId();
        List<Item> items = itemRepository.findAllByRequestId(id);

        return new RequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemMapper.toListItemDto(items)
        );
    }

    public ItemRequest toItemRequest(RequestDto requestDto) {
        return new ItemRequest(
                requestDto.getDescription()
        );
    }
}
