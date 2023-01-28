package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@Component
public class BookingMapper {
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    public BookingMapper(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

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
