package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Component
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Booking> create(@Valid @RequestBody CreateBookingDto bookingDto,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        userService.findById(ownerId);
        return new ResponseEntity<>(bookingService.create(bookingDto, ownerId), HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(@PathVariable Long bookingId,
                           @RequestParam Boolean approved,
                           @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userService.findById(userId);
        return bookingService.changeStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking find(@PathVariable Long bookingId, @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userService.findById(userId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> findAllByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userService.findById(userId);
        return bookingService.findAllByUser(userId, validateState(state));
    }

    @GetMapping("/owner")
    public List<Booking> findAllUserItems(@RequestParam(required = false, defaultValue = "ALL") String state,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        userService.findById(userId);
        return bookingService.findAllByOwner(userId, validateState(state));
    }

    private State validateState(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new WrongParametersException("Unknown state: " + state);
        }

    }
}
