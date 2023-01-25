package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AlreadyBookedException;
import ru.practicum.shareit.exceptions.AuntificationException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    BookingRepository repository;

    @Override
    public List<Booking> findAllByUser(Long userId, State state) {
        switch (state) {
            case ALL:
                return repository.findAllByUser(userId);
            case CURRENT:
                return repository.findCurrentByUser(userId);
            case PAST:
                return repository.findPastByUser(userId);
            case FUTURE:
                return repository.findFutureByUser(userId);
            case WAITING:
            case REJECTED:
                return repository.findByStatusAndUser(userId, state);
            default:
                throw new NotFoundAnythingException("Передан неверный статус");
        }
    }

    @Override
    public List<Booking> findAllByOwner(Long userId, State state) {
        switch (state) {
            case ALL:
                return repository.findAllByOwner(userId);
            case CURRENT:
                return repository.findCurrentByOwner(userId);
            case PAST:
                return repository.findPastByOwner(userId);
            case FUTURE:
                return repository.findFutureByOwner(userId);
            case WAITING:
            case REJECTED:
                return repository.findByStatusAndOwner(userId, state);
            default:
                throw new NotFoundAnythingException("Передан неверный статус");
        }
    }

    @Override
    public Booking findById(Long id, Long userId) {
        Booking booking = repository.findById(id).orElseThrow(() ->
                new NotFoundAnythingException("Бронирования с данным id не существует"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new AuntificationException("У вас нет доступа к получению данных об этом бронировании");
        }
    }

    @Override
    public Booking create(Booking booking, Long userId) {
        if (booking.getBooker().getId().equals(userId)) {
            throw new AuntificationException("Невозможно забронировать собственную вещь");
        }
        if (!booking.getItem().getAvailable()) {
            throw new AlreadyBookedException("Эта вещь уже забронирована!");
        }
        log.debug("Добавлено новое бронирование: {}", booking);
        booking.setStatus(StatusOfBooking.WAITING);
        return repository.save(booking);
    }

    @Override
    public Booking changeStatus(Long bookingId, Long userId, Boolean approved) {
        Booking booking = repository.findById(bookingId).orElseThrow(() ->
                new NotFoundAnythingException("Бронирования с данным id не существует"));
        if (!booking.getItem().getAvailable()) {
            throw new AlreadyBookedException("Эта вещь уже забронирована!");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (approved) {
                booking.setStatus(StatusOfBooking.APPROVED);
            } else {
                booking.setStatus(StatusOfBooking.REJECTED);
            }
            return repository.save(booking);
        } else {
            throw new AuntificationException("У вас нет доступа к изменению статуса этого бронирования");
        }
    }
}
