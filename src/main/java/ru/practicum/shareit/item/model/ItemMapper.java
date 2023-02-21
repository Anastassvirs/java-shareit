package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.request.RequestRepository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ItemMapper {
    private final RequestRepository requestRepository;

    public ItemDto toItemDto(@NotNull Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public ItemDtoBookingsComments toItemDtoBookingsComments(@NotNull Item item) {
        return new ItemDtoBookingsComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public List<ItemDto> toListItemDto(@NotNull List<Item> items) {
        List<ItemDto> dtoList = new ArrayList<>();
        for (Item item : items) {
            dtoList.add(new ItemDto(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            item.getRequest() != null ? item.getRequest().getId() : null
                    )
            );
        }
        return dtoList;
    }

    public Item toItem(@NotNull ItemDto itemDto) {
        return new Item(
                itemDto.getId() != null ? itemDto.getId() : null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId() != null ?
                        requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                                new NotFoundAnythingException("Вещи с данным id не существует")) : null
        );
    }
}
