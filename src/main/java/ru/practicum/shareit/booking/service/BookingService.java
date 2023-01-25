package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    List<Booking> findAllByUser(Long userId, State state);

    List<Booking> findAllUserItems(Long userId, State state);

    Booking findById(Long id, Long userId);

    Booking create(Booking booking);

    Booking changeStatus(Long bookingId, Long userId, Boolean approved);
}
