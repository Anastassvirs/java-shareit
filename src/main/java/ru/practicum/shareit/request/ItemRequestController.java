package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private final RequestService service;

    @PostMapping
    public ResponseEntity<ItemRequest> create(@Valid @RequestBody RequestDto request,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return new ResponseEntity<>(service.create(request, userId), HttpStatus.OK);
    }


    @GetMapping
    public List<RequestDto> findAllByOwnerWithResponses(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return service.findAllByOwnerWithResponses(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return service.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto findByIdWithResponses(@PathVariable Long requestId,
                                            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return service.findByIdWithResponses(requestId, userId);
    }
}
