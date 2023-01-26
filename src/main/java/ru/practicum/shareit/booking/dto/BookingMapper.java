package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@RequiredArgsConstructor
public class BookingMapper {
    private UserService userService;
    private ItemService itemService;

    public Booking toBooking(BookingDto bookingCreationDto) {
        return new Booking(
                bookingCreationDto.getStart(),
                bookingCreationDto.getEnd(),
                itemService.findById(bookingCreationDto.getItemId()),
                userService.findById(bookingCreationDto.getBookerId())
        );
    }
}
