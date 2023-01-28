package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    List<Booking> findAllByUser(Long userId, State state);

    List<Booking> findAllByOwner(Long userId, State state);

    Booking findById(Long id, Long userId);

    Booking create(CreateBookingDto bookingDto, Long userId);

    Booking changeStatus(Long bookingId, Long userId, Boolean approved);
}
