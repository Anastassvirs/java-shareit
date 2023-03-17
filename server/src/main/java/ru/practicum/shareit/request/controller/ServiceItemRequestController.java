package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ServiceItemRequestController {
    private final RequestService service;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequest> create(@RequestBody ItemRequestDto request,
                                              @RequestHeader(value = userIdHeader) Long userId) {
        return new ResponseEntity<>(service.create(request, userId), HttpStatus.OK);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByOwnerWithResponses(@RequestHeader(value = userIdHeader) Long userId) {
        return service.findAllByOwnerWithResponses(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestHeader(value = userIdHeader) Long userId) {
        return service.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByIdWithResponses(@PathVariable Long requestId,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return service.findByIdWithResponses(requestId, userId);
    }
}
