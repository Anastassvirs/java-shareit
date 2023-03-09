package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDtoBookingsComments> findAllByUser(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size,
                                                       @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.findAllByUser(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoBookingsComments> findAllByText(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size,
                                                       @RequestParam String text,
                                                       @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.findAllByText(from, size, text, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBookingsComments find(@PathVariable Long itemId,
                                        @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.findDtoById(itemId, userId);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto item,
                          @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                          @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody ItemDto item, @RequestHeader(value = userIdHeader) Long userId) {
        itemService.deleteItem(item.getId(), userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @RequestHeader(value = userIdHeader) Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
