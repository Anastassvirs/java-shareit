package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.AlreadyBookedException;
import ru.practicum.shareit.exceptions.AuntificationException;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingsServiceTests {
    @Autowired
    ObjectMapper mapper;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    BookingMapper bookingMapper;

    @Test
    public void findAllByUserTest() {
        Integer from = 0, size = 1;
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);
        Booking booking2 = new Booking(bookingId, start, end, item, user, StatusOfBooking.REJECTED);
        Booking booking3 = new Booking(bookingId, start.minusDays(5), end.minusDays(3), item, user, StatusOfBooking.APPROVED);
        Booking booking4 = new Booking(bookingId, start.plusDays(3), end.plusDays(5), item, user, StatusOfBooking.WAITING);
        List<Booking> allBookings = List.of(booking, booking2, booking3, booking4);
        List<Booking> currentBookings = List.of(booking, booking2);
        List<Booking> pastBookings = List.of(booking3);
        List<Booking> futureBookings = List.of(booking4);
        List<Booking> waitingBookings = List.of(booking, booking4);
        List<Booking> rejectedBookings = List.of(booking2);

        when(userService.userExistById(any(Long.class))).thenReturn(true);

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Booking> page = new PageImpl<>(allBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdOrderByEndDesc(userId, pageable)).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.ALL), allBookings);

        page = new PageImpl<>(currentBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.CURRENT), currentBookings);

        page = new PageImpl<>(pastBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.PAST), pastBookings);

        page = new PageImpl<>(futureBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByEndDesc(
                any(Long.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.FUTURE), futureBookings);

        page = new PageImpl<>(waitingBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(any(Long.class),
                any(StatusOfBooking.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.WAITING), waitingBookings);

        page = new PageImpl<>(rejectedBookings, pageable, 1);
        when(bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(any(Long.class),
                any(StatusOfBooking.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByUser(from, size, userId, State.REJECTED), rejectedBookings);
    }

    @Test
    public void findAllByOwnerTest() {
        Integer from = 0, size = 1;
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);
        Booking booking2 = new Booking(bookingId, start, end, item, user, StatusOfBooking.REJECTED);
        Booking booking3 = new Booking(bookingId, start.minusDays(5), end.minusDays(3), item, user, StatusOfBooking.APPROVED);
        Booking booking4 = new Booking(bookingId, start.plusDays(3), end.plusDays(5), item, user, StatusOfBooking.WAITING);
        List<Booking> allBookings = List.of(booking, booking2, booking3, booking4);
        List<Booking> currentBookings = List.of(booking, booking2);
        List<Booking> pastBookings = List.of(booking3);
        List<Booking> futureBookings = List.of(booking4);
        List<Booking> waitingBookings = List.of(booking, booking4);
        List<Booking> rejectedBookings = List.of(booking2);

        when(userService.userExistById(any(Long.class))).thenReturn(true);

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Booking> page = new PageImpl<>(allBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdOrderByEndDesc(userId, pageable)).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.ALL), allBookings);

        page = new PageImpl<>(currentBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.CURRENT), currentBookings);

        page = new PageImpl<>(pastBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByEndDesc(
                any(Long.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.PAST), pastBookings);

        page = new PageImpl<>(futureBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByEndDesc(
                any(Long.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.FUTURE), futureBookings);

        page = new PageImpl<>(waitingBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(any(Long.class),
                any(StatusOfBooking.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.WAITING), waitingBookings);

        page = new PageImpl<>(rejectedBookings, pageable, 1);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(any(Long.class),
                any(StatusOfBooking.class), any(Pageable.class))).thenReturn(page);
        assertEquals(bookingService.findAllByOwner(from, size, userId, State.REJECTED), rejectedBookings);
    }

    @Test
    public void findByIdTest() {
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userService.userExistById(any(Long.class))).thenReturn(true);

        assertEquals(bookingService.findById(bookingId, userId), booking);
    }

    @Test
    public void findByIdErrorsTest() {
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            bookingService.findById(bookingId, userId + 1L);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого происходит поиск бронирования, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        thrown = catchThrowable(() -> {
            bookingService.findById(bookingId, userId + 1L);
        });
        assertThat(thrown).isInstanceOf(AuntificationException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("У вас нет доступа к получению данных об этом бронировании", thrown.getMessage());

    }

    @Test
    public void saveTest() {
        Long bookingId = 1L, userId = 1L, ownerId = 2L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(ownerId, "hehehaha", "hoho.hyhy@mail.com");
        User owner = new User(ownerId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", owner, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);
        CreateBookingDto bookingDto = new CreateBookingDto(start, end, itemId);

        when(bookingRepository.save(booking)).thenReturn(booking);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemService.itemExistById(any(Long.class))).thenReturn(true);
        when(itemService.findById(itemId)).thenReturn(item);
        when(userService.findById(ownerId)).thenReturn(user);
        when(bookingMapper.toBookingCreation(bookingDto)).thenReturn(booking);

        assertEquals(bookingService.create(bookingDto, userId), booking);
    }

    @Test
    public void saveErrorTest() {
        Long userId = 1L, ownerId = 2L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(ownerId, "hehehaha", "hoho.hyhy@mail.com");
        User owner = new User(ownerId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", owner, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        CreateBookingDto bookingDto = new CreateBookingDto(start, end, itemId);

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            bookingService.create(bookingDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого создается бронирование, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemService.itemExistById(any(Long.class))).thenReturn(false);
        thrown = catchThrowable(() -> {
            bookingService.create(bookingDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Вещи, на которую создается бронирование, не существует", thrown.getMessage());

        when(itemService.itemExistById(any(Long.class))).thenReturn(true);
        when(itemService.findById(itemId)).thenReturn(item);
        when(userService.findById(ownerId)).thenReturn(user);
        thrown = catchThrowable(() -> {
            bookingService.create(bookingDto, ownerId);
        });
        assertThat(thrown).isInstanceOf(AuntificationException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Невозможно забронировать собственную вещь", thrown.getMessage());

        item = new Item(itemId, "itemName", "descdesc", false, user, itemRequest);
        when(itemService.findById(itemId)).thenReturn(item);
        thrown = catchThrowable(() -> {
            bookingService.create(bookingDto, userId);
        });
        assertThat(thrown).isInstanceOf(AlreadyBookedException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Эта вещь уже забронирована!", thrown.getMessage());
    }

    @Test
    public void saveWrongStartEndTest() {
        Long userId = 1L, ownerId = 2L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(ownerId, "hehehaha", "hoho.hyhy@mail.com");
        User owner = new User(ownerId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", owner, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        CreateBookingDto bookingDto = new CreateBookingDto(end, start, itemId);

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemService.itemExistById(any(Long.class))).thenReturn(true);
        when(itemService.findById(itemId)).thenReturn(item);
        when(userService.findById(ownerId)).thenReturn(user);

        Throwable thrown = catchThrowable(() -> {
            bookingService.create(bookingDto, userId);
        });
        assertThat(thrown).isInstanceOf(WrongParametersException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Введены некорректные параметры даты старта/окончания бронирования", thrown.getMessage());
    }

    @Test
    public void changeStatusTest() {
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "hehehaha", "hoho.hyhy@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.REJECTED);
        when(bookingRepository.save(booking)).thenReturn(booking);
        assertEquals(bookingService.changeStatus(bookingId, userId, false), booking);

        booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.APPROVED);
        when(bookingRepository.save(booking)).thenReturn(booking);
        assertEquals(bookingService.changeStatus(bookingId, userId, true), booking);
    }

    @Test
    public void changeStatusErrorsTest() {
        Long bookingId = 1L, userId = 1L, itemId = 1L, requestId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        User user = new User(userId, "hehehaha", "hoho.hyhy@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "desc", user, LocalDateTime.now().minusDays(2));
        Item item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        Booking booking;

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            bookingService.changeStatus(bookingId, userId, true);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого изменяется бронирование, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        thrown = catchThrowable(() -> {
            bookingService.changeStatus(bookingId, userId, true);
        });
        assertThat(thrown).isInstanceOf(AlreadyBookedException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Эта бронь уже подтверждена!", thrown.getMessage());

        item = new Item(itemId, "itemName", "descdesc", false, user, itemRequest);
        booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        thrown = catchThrowable(() -> {
            bookingService.changeStatus(bookingId, userId, true);
        });
        assertThat(thrown).isInstanceOf(AlreadyBookedException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Эта вещь уже забронирована!", thrown.getMessage());

        item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        thrown = catchThrowable(() -> {
            bookingService.changeStatus(bookingId, 2L, true);
        });
        assertThat(thrown).isInstanceOf(AuntificationException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("У вас нет доступа к изменению статуса этого бронирования", thrown.getMessage());

    }
}
