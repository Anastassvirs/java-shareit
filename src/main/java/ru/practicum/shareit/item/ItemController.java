package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/{userId}")
    public Item find(@PathVariable Long userId) {
        return itemService.findById(userId);
    }

    @PostMapping
    public Item create(@Valid @RequestBody Item item) {
        return itemService.createItem(item);
    }

    @PutMapping
    public Item update(@Valid @RequestBody Item item) {
        return itemService.updateItem(item);
    }

    @DeleteMapping()
    public Item delete(@Valid @RequestBody Item item) {
        return itemService.deleteItem(item);
    }
}
