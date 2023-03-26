package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findAllByUser(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllByText(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestParam String text,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findAllByText(from, size, text, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> find(@PathVariable Long itemId,
                                       @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.findDtoById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto item,
                                         @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                         @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody ItemDto item, @RequestHeader(value = userIdHeader) Long userId) {
        itemClient.deleteItem(item.getId(), userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return itemClient.createComment(commentDto, itemId, userId);
    }
}
