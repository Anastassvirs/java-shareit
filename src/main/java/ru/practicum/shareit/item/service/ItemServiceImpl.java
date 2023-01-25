package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    public List<Item> findAll() {
        return repository.findAll();
    }

    public List<Item> findAllByUser(Long userId) {
        return repository.findAllByOwner(userId);
    }

    public List<Item> findAllByText(String text) {
        return repository.findAllByText(text);
    }

    public Item findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundAnythingException("Вещи с данным id не существует"));
    }

    public Item createItem(ItemDto itemDto, User owner) {
        log.debug("Пользователем с id: {} была добавлена новая вещь: {}", owner.getId(), itemDto);
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        return repository.save(newItem);
    }

    public Item updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item item = findById(itemId);
        if (ownerId.equals(item.getOwner().getId())) {
            Optional.ofNullable(itemDto.getName()).ifPresent(item::setName);
            Optional.ofNullable(itemDto.getDescription()).ifPresent(item::setDescription);
            Optional.ofNullable(itemDto.getAvailable()).ifPresent(item::setAvailable);
            log.debug("Обновлена вещь: {}", item);
            return repository.save(item);
        } else {
            log.debug("Произошла ошибка: ID пользователя не соответсвует владельцу вещи");
            throw new NotFoundAnythingException("ID пользователя не соответсвует владельцу вещи");
        }

    }

    public void deleteItem(Long itemId) {
        repository.deleteById(itemId);
    }
}
