package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

@Component
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

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId(),
                booking.getItem().getId()
        );
    }
}
