package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

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
        try {
            userService.findById(ownerId);
            return new ResponseEntity<>(itemService.findAllByUser(ownerId), HttpStatus.OK);
        } catch (NotFoundAnythingException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> findAllByText(@RequestParam String text, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        try {
            userService.findById(ownerId);
            return new ResponseEntity<>(itemService.findAllByText(text), HttpStatus.OK);
        } catch (NotFoundAnythingException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{itemId}")
    public Item find(@PathVariable Long userId) {
        return itemService.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Item> create(@Valid @RequestBody ItemDto item, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        try {
            User owner = userService.findById(ownerId);
            return new ResponseEntity<>(itemService.createItem(item, owner), HttpStatus.OK);
        } catch (NotFoundAnythingException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Item> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto, @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        try {
            Item item = itemService.findById(itemId);
            if (ownerId == item.getOwner().getId()) {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
                itemService.updateItem(find(itemId));
                return new ResponseEntity<>(item, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NotFoundAnythingException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping()
    public Item delete(@Valid @RequestBody Item item) {
        return itemService.deleteItem(item);
    }
}
