package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("memoryItemStorage")
public class InMemoryItemStorage implements ItemStorage{
    private HashMap<Long, Item> items;
    private Long numberOfItems;
    private List<Item> listItems;

    public InMemoryItemStorage() {
        items = new HashMap();
        listItems = new ArrayList<>();
        numberOfItems = (long) 0;
    }

    @Override
    public List<Item> findAll() {
        listItems = new ArrayList();
        listItems.addAll(items.values());
        return listItems;
    }


    @Override
    public List<Item> findAllByUser(Long userId) {
        listItems = new ArrayList();
        for (Item item: items.values()) {
            if (item.getOwner().getId() == userId) {
                listItems.add(item);
            }
        }
        return listItems;
    }

    @Override
    public List<Item> findAllByText(String text) {
        listItems = new ArrayList();
        if (text.equals("")) {
            return listItems;
        }
        for (Item item: items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                listItems.add(item);
            }
        }
        return listItems;
    }

    @Override
    public Item findById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundAnythingException("Искомая вещь не существует");
        }
    }

    @Override
    public Item saveItem(Item item) {
        numberOfItems++;
        item.setId(numberOfItems);
        items.put(numberOfItems, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item deleteItem(Item item) {
        items.remove(item.getId());
        return item;
    }
}
