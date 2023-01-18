package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Item>> findAllByUser(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userService.findById(ownerId);
        return new ResponseEntity<>(itemService.findAllByUser(ownerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> findAllByText(@RequestParam String text, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userService.findById(ownerId);
        return new ResponseEntity<>(itemService.findAllByText(text), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public Item find(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody ItemDto item, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return new ResponseEntity<>(itemService.createItem(item, userService.findById(ownerId)), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        Item item = itemService.findById(itemId);
        if (ownerId.equals(item.getOwner().getId())) {
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
            Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
            itemService.updateItem(find(itemId));
            return new ResponseEntity<>(item, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping()
    public Item delete(@Valid @RequestBody Item item) {
        return itemService.deleteItem(item);
    }
}
