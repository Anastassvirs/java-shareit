package ru.practicum.shareit.request.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Qualifier("memoryRequestStorage")
public class InMemoryRequestStorage implements RequestStorage {
    private HashMap<Long, ItemRequest> requests;
    private Long numberOfRequests;

    public InMemoryRequestStorage() {
        requests = new HashMap();
        numberOfRequests = (long) 0;
    }

    @Override
    public List<ItemRequest> findAll() {
        List<ItemRequest> listrequests = new ArrayList();
        listrequests.addAll(requests.values());
        return listrequests;
    }

    @Override
    public ItemRequest findById(Long id) {
        if (requests.containsKey(id)) {
            return requests.get(id);
        } else {
            throw new NotFoundAnythingException("Искомый пользователь не существует");
        }
    }

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        numberOfRequests++;
        itemRequest.setId(numberOfRequests);
        requests.put(numberOfRequests, itemRequest);
        return itemRequest;
    }

    @Override
    public ItemRequest update(ItemRequest itemRequest) {
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public void delete(Long id) {
        requests.remove(id);
    }
}
