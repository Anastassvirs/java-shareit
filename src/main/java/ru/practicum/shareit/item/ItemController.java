package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Item;
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

    @GetMapping
    public List<ItemDtoBookingsComments> findAllByUser(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size,
                                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.findAllByUser(from, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoBookingsComments> findAllByText(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size,
                                                       @RequestParam String text,
                                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.findAllByText(from, size, text, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBookingsComments find(@PathVariable Long itemId,
                                        @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.findDtoById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto item,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.createItem(item, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.updateItem(itemId, itemDto, userId), HttpStatus.OK);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(item.getId(), userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable Long itemId,
                                                    @Valid @RequestBody CommentDto commentDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(itemService.createComment(commentDto, itemId, userId), HttpStatus.OK);
    }
}
