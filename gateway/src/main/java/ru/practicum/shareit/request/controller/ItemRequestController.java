package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final RequestClient requestClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto request,
                                         @RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.create(request, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerWithResponses(@RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.findAllByOwnerWithResponses(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size,
                                          @RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByIdWithResponses(@PathVariable Long requestId,
                                                        @RequestHeader(value = userIdHeader) Long userId) {
        return requestClient.findByIdWithResponses(requestId, userId);
    }
}
