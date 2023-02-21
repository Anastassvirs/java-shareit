package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserService userService;
    private final RequestMapper requestMapper;

    public ItemRequest findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundAnythingException("Запроса с данным id не существует"));
    }

    @Override
    @Transactional
    public ItemRequest create(ItemRequestDto request, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого происходит поиск запросов, не существует");
        }
        log.debug("Добавлен новый запрос: {}", request);
        ItemRequest newRequest = requestMapper.toItemRequest(request);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequestor(userService.findById(userId));
        return repository.save(newRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByOwnerWithResponses(Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого происходит поиск запросов, не существует");
        }
        List<ItemRequest> requests = repository.findAllByRequestorId(userId);
        return requestMapper.toListRequestDto(requests);
    }

    @Override
    public List<ItemRequestDto> findAll(Integer from, Integer size, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого происходит поиск запросов, не существует");
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return requestMapper.toListRequestDto(repository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable).getContent());
    }

    @Override
    public ItemRequestDto findByIdWithResponses(Long requestId, Long userId) {
        if (!userService.userExistById(userId)) {
            throw new NotFoundAnythingException("Пользователя, от лица которого происходит поиск запросов, не существует");
        }
        return requestMapper.toRequestDto(repository.findById(requestId).orElseThrow(() ->
                new NotFoundAnythingException("Запроса с данным id не существует")));
    }

    public ItemRequest updateItemRequest(ItemRequest request) {
        if (requestAlreadyExist(request.getId())) {
            log.debug("Обновлен запрос: {}", request);
            return repository.save(request);
        } else {
            log.debug("Произошла ошибка: Введенного запроса не существует");
            throw new NotFoundAnythingException("Такого запроса не существует");
        }
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private boolean requestAlreadyExist(Long requestId) {
        for (ItemRequest oldRequest : repository.findAll()) {
            if (Objects.equals(oldRequest.getId(), requestId)) {
                return true;
            }
        }
        return false;
    }
}
