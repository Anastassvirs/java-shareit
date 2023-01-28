package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AlreadyBookedException;
import ru.practicum.shareit.exceptions.AuntificationException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository repository, ItemServiceImpl itemService, UserServiceImpl userService, BookingMapper bookingMapper) {
        this.repository = repository;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
    }

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
                return repository.findByStatusAndUser(userId, StatusOfBooking.WAITING);
            case REJECTED:
                return repository.findByStatusAndUser(userId, StatusOfBooking.REJECTED);
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
                return repository.findByStatusAndOwner(userId, StatusOfBooking.WAITING);
            case REJECTED:
                return repository.findByStatusAndOwner(userId, StatusOfBooking.REJECTED);
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

    @Transactional
    @Override
    public Booking create(CreateBookingDto bookingDto, Long userId) {
        Item item = itemService.findById(bookingDto.getItemId());
        userService.findById(userId);
        User owner = userService.findById(item.getOwner().getId());
        if (owner.getId().equals(userId)) {
            throw new AuntificationException("Невозможно забронировать собственную вещь");
        }
        if (!item.getAvailable()) {
            throw new AlreadyBookedException("Эта вещь уже забронирована!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new WrongParametersException("Введены некорректные параметры даты старта/окончания бронирования");
        }
        log.debug("Добавлено новое бронирование: {}", bookingDto);
        Booking booking = bookingMapper.toBookingCreation(bookingDto);
        booking.setBooker(userService.findById(userId));
        return repository.save(booking);
    }

    @Transactional
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
