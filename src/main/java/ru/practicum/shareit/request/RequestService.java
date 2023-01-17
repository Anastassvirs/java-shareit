package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.RequestStorage;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class RequestService {
    private final RequestStorage storage;

    @Autowired
    public RequestService(@Qualifier("memoryRequestStorage") RequestStorage storage) {
        this.storage = storage;
    }

    public List<ItemRequest> findAll() {
        return storage.findAll();
    }

    public ItemRequest findById(Long id) {
        return storage.findById(id);
    }

    public ItemRequest createItemRequest(ItemRequest request) {
        if (requestAlreadyExist(request)) {
            log.debug("Произошла ошибка: Введенный запрос уже зарегистрирован");
            throw new AlreadyExistException("Такой запрос уже зарегистрирован");
        }
        log.debug("Добавлен новый запрос: {}", request);
        return storage.save(request);
    }

    public ItemRequest updateItemRequest(ItemRequest request) {
        if (requestAlreadyExist(request)) {
            log.debug("Обновлен запрос: {}", request);
            return storage.update(request);
        } else {
            log.debug("Произошла ошибка: Введенного запроса не существует");
            throw new NotFoundAnythingException("Такого запроса не существует");
        }
    }

    public void deleteById(Long id) {
        storage.delete(id);
    }

    private boolean requestAlreadyExist(ItemRequest request) {
        for (ItemRequest OldRequest: storage.findAll()) {
            if (Objects.equals(OldRequest.getId(), request.getId())) {
                return true;
            }
        }
        return false;
    }
}
