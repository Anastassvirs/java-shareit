package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Component
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody CreateBookingDto bookingDto,
                                         @RequestHeader(value = userIdHeader) Long userId) {
        return bookingClient.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(value = userIdHeader) Long userId) {
        return bookingClient.changeStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> find(@PathVariable Long bookingId, @RequestHeader(value = userIdHeader) Long userId) {
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUser(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                @RequestParam(required = false, defaultValue = "ALL") String state,
                                                @RequestHeader(value = userIdHeader) Long userId) {
        return bookingClient.findAllByUser(from, size, userId, State.validateState(state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllUserItems(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state,
                                                   @RequestHeader(value = userIdHeader) Long userId) {
        return bookingClient.findAllByOwner(from, size, userId, State.validateState(state));
    }
}
