package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.repository.ItemRepository;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;

    public Booking toBookingCreation(CreateBookingDto bookingCreationDto) {
        return new Booking(
                bookingCreationDto.getStart(),
                bookingCreationDto.getEnd(),
                itemRepository.findById(bookingCreationDto.getItemId()).orElseThrow(() ->
                        new NotFoundAnythingException("Такой вещи не существует"))
        );
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getItem().getId()
        );
    }
}
