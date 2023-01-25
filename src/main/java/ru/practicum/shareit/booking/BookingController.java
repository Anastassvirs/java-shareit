package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceImpl bookingService;
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public Booking create(@Valid @RequestBody Booking booking,
                          @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        booking.setBooker(userServiceImpl.findById(ownerId));
        return bookingService.create(booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@PathVariable Long bookingId,
                           @RequestParam Boolean approved,
                           @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userServiceImpl.findById(userId);
        return bookingService.changeStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking find(@PathVariable Long bookingId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userServiceImpl.findById(userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> findAllByUser(@RequestParam(required = false, defaultValue = "ALL") State state,
                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userServiceImpl.findById(userId);
        return bookingService.findAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> findAllUserItems(@RequestParam(required = false, defaultValue = "ALL") State state,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userServiceImpl.findById(userId);
        return bookingService.findAllUserItems(userId, state);
    }
}
