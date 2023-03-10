package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Component
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public Booking create(@Valid @RequestBody CreateBookingDto bookingDto,
                          @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@PathVariable Long bookingId,
                           @RequestParam Boolean approved,
                           @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.changeStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking find(@PathVariable Long bookingId, @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> findAllByUser(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                       @RequestParam(defaultValue = "10") @Positive Integer size,
                                       @RequestParam(required = false, defaultValue = "ALL") String state,
                                       @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findAllByUser(from, size, userId, State.validateState(state));
    }

    @GetMapping("/owner")
    public List<Booking> findAllUserItems(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size,
                                          @RequestParam(required = false, defaultValue = "ALL") String state,
                                          @RequestHeader(value = userIdHeader) Long userId) {
        return bookingService.findAllByOwner(from, size, userId, State.validateState(state));
    }
}
