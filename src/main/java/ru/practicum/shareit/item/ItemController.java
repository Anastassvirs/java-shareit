package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @GetMapping
    public ResponseEntity<List<Item>> findAllByUser(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userServiceImpl.findById(ownerId);
        return new ResponseEntity<>(itemServiceImpl.findAllByUser(ownerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> findAllByText(@RequestParam String text, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userServiceImpl.findById(ownerId);
        return new ResponseEntity<>(itemServiceImpl.findAllByText(text), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public Item find(@PathVariable Long itemId) {
        return itemServiceImpl.findById(itemId);
    }

    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody ItemDto item, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userServiceImpl.findById(ownerId);
        return new ResponseEntity<>(itemServiceImpl.createItem(item, userServiceImpl.findById(ownerId)), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userServiceImpl.findById(ownerId);
        return new ResponseEntity<>(itemServiceImpl.updateItem(itemId, itemDto, ownerId), HttpStatus.OK);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Item item, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userServiceImpl.findById(ownerId);
        itemServiceImpl.deleteItem(item.getId());
    }
}
