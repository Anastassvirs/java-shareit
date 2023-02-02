package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@Component
@AllArgsConstructor
public class BookingMapper {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking toBooking(BookingDto bookingCreationDto) {
        return new Booking(
                bookingCreationDto.getStart(),
                bookingCreationDto.getEnd(),
                itemRepository.findById(bookingCreationDto.getItemId()).orElseThrow(() ->
                        new NotFoundAnythingException("Такой вещи не существует")),
                userRepository.findById(bookingCreationDto.getBookerId()).orElseThrow(() ->
                        new NotFoundAnythingException("Такого пользователя не существует"))
        );
    }

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
                booking.getBooker().getId(),
                booking.getItem().getId()
        );
    }
}
