package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.dto.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ItemService {
    private final ItemStorage storage;

    @Autowired
    public ItemService(@Qualifier("memoryItemStorage") ItemStorage storage) {
        this.storage = storage;
    }

    public List<Item> findAll() {
        return storage.findAll();
    }

    public List<Item> findAllByUser(Long userId) {
        return storage.findAllByUser(userId);
    }

    public List<Item> findAllByText(String text) {
        return storage.findAllByText(text);
    }

    public Item findById(Long id) {
        return storage.findById(id);
    }

    public Item createItem(ItemDto itemDto, User owner) {
        log.debug("Пользователем с id: {} была добавлена новая вещь: {}", owner.getId(), itemDto);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        return storage.saveItem(newItem);
    }

    public Item updateItem(Item item) {
        if (itemAlreadyExist(item)) {
            log.debug("Обновлена вещь: {}", item);
            return storage.updateItem(item);
        } else {
            log.debug("Произошла ошибка: Введенной вещи не существует");
            throw new NotFoundAnythingException("Такой вещи не существует");
        }
    }

    public Item deleteItem(Item item) {
        return storage.deleteItem(item);
    }

    private boolean itemAlreadyExist(Item item) {
        for (Item oldItem: storage.findAll()) {
            if (Objects.equals(oldItem.getId(), item.getId())) {
                return true;
            }
        }
        return false;
    }
}
