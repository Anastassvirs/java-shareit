package ru.practicum.shareit.item.storage;

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
    public Item findById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundAnythingException("Искомая вещь не существует");
        }
    }

    @Override
    public Item saveItem(Item user) {
        numberOfItems++;
        user.setId(numberOfItems);
        items.put(numberOfItems, user);
        return user;
    }

    @Override
    public Item updateItem(Item user) {
        items.put(user.getId(), user);
        return user;
    }

    @Override
    public Item deleteItem(Item user) {
        items.remove(user.getId());
        return user;
    }
}
