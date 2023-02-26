package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.exceptions.WrongParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {
    @Autowired
    ObjectMapper mapper;

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    UserService userService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemMapper itemMapper;

    @Mock
    CommentMapper commentMapper;

    @Mock
    BookingMapper bookingMapper;

    @Test
    public void findAllTest() {
        when(itemRepository.findAll()).thenReturn(List.of());
        assertEquals(List.of(), itemService.findAll());
    }

    @Test
    public void findAllByUserTest() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L, userId = 1L, bookingId = 1L, bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "Some description", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        BookingDto bookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        Booking booking = new Booking(bookerId, start, end, item,
                new User(bookerId, "name", "email@gmail.com"), StatusOfBooking.WAITING);
        Comment comment = new Comment(commentId, commentText, item, user, created);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);
        Integer from = 0, size = 1;
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<ItemDtoBookingsComments> itemDtos = List.of(itemDto);
        List<Item> items = List.of(item);
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);

        Pageable pageable = PageRequest.of(from / size, size);
        when(itemRepository.findAllByOwnerIdOrderById(userId, pageable)).thenReturn(items);
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        assertEquals(itemService.findAllByUser(from, size, userId), itemDtos);
    }

    @Test
    public void findAllByUserErrorTest() {
        Long userId = 1L;
        Integer from = 0, size = 1;
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.findAllByUser(from, size, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, по которому производится поиск вещи, не существует", thrown.getMessage());
    }

    @Test
    public void findAllByTextTest() {
        String text = "DeScr";
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L, userId = 1L, bookingId = 1L, bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "Some description", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        BookingDto bookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        Booking booking = new Booking(bookerId, start, end, item,
                new User(bookerId, "name", "email@gmail.com"), StatusOfBooking.WAITING);
        Comment comment = new Comment(commentId, commentText, item, user, created);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);
        Integer from = 0, size = 1;
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<ItemDtoBookingsComments> itemDtos = List.of(itemDto);
        List<Item> items = List.of(item);
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);

        Pageable pageable = PageRequest.of(from / size, size);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(itemRepository.findAllByText(text, pageable)).thenReturn(items);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        assertEquals(itemService.findAllByText(from, size, text, userId), itemDtos);
    }

    @Test
    public void findAllByTextErrorsTest() {
        String text = "DeScr";
        Long userId = 1L;
        Integer from = 0, size = 1;

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.findAllByText(from, size, text, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится поиск вещи, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        assertEquals(itemService.findAllByText(from, size, "", userId), List.of());
    }

    @Test
    public void findDtoByIdTest() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L, userId = 1L, bookingId = 1L, bookerId = 2L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "Some description", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        BookingDto bookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        Booking booking = new Booking(bookerId, start, end, item,
                new User(bookerId, "name", "email@gmail.com"), StatusOfBooking.WAITING);
        Comment comment = new Comment(commentId, commentText, item, user, created);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdAndItemIdAndStartAfterOrderByStartDesc(any(Long.class), any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndItemIdAndEndBeforeOrderByEndDesc(any(Long.class), any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(itemService.findDtoById(itemId, userId), itemDto);
    }

    @Test
    public void findDtoByIdErrorTest() {
        Long itemId = 1L, userId = 1L;
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.findDtoById(itemId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится поиск вещи, не существует", thrown.getMessage());
    }

    @Test
    public void findByIdTest() {
        Long itemId = 1L, userId = 1L;
        Item item = new Item(itemId, "Name", "Some description", true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(itemService.findById(itemId), item);
    }

    @Test
    public void saveTest() {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Name", "Some description", true);
        Item item = new Item(itemId, "Name", "Some description", true);

        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);

        assertEquals(itemDto, itemService.createItem(itemDto, userId));
    }

    @Test
    public void saveNoUserTest() {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Name", "Some description", true);

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.createItem(itemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится создание вещи, не существует", thrown.getMessage());
    }

    @Test
    public void updateTest() {
        Long itemId = 1L, userId = 1L;
        User user = new User(userId, "Anastasia", "an.svir@mail.com");
        ItemDto itemDto = new ItemDto(itemId, "Name", "Some description", true);
        Item item = new Item(itemId, "Name", "Some description", true, user);

        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(itemDto, itemService.updateItem(itemId, itemDto, userId));
    }

    @Test
    public void updateErrorsTest() {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Name", "Some description", true);
        User user = new User(userId, "Anastasia", "an.svir@mail.com");
        Item item = new Item(itemId, "Name", "Some description", true, user);
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, itemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится изменение вещи, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of());
        thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, itemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Вещи, которую вы пытаетесь изменить, не существует", thrown.getMessage());

        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, itemDto, userId + 1L);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("ID пользователя не соответсвует владельцу вещи", thrown.getMessage());

    }

    @Test
    public void deleteTest() {
        Long itemId = 1L, userId = 1L;
        User user = new User(userId, "Anastasia", "an.svir@mail.com");
        Item item = new Item(itemId, "Name", "Some description", true, user);

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, userId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void deleteErrorsTest() {
        Long itemId = 1L, userId = 1L;
        User user = new User(userId, "Anastasia", "an.svir@mail.com");
        Item item = new Item(itemId, "Name", "Some description", true, user);

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.deleteItem(itemId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого удаляется комментарий, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of());
        thrown = catchThrowable(() -> {
            itemService.deleteItem(itemId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Вещи, которую вы пытаетесь удалить, не существует", thrown.getMessage());

        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        thrown = catchThrowable(() -> {
            itemService.deleteItem(itemId, userId + 1L);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("ID пользователя не соответсвует владельцу вещи", thrown.getMessage());

    }

    @Test
    public void saveCommentTest() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L, userId = 1L, bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "description of item", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        Comment comment = new Comment(commentId, commentText, item, user, created);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);
        Booking booking = new Booking(bookingId, start, end, item, user, StatusOfBooking.WAITING);

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.findById(any(Long.class))).thenReturn(user);
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentMapper.newtoComment(any(CommentDto.class),
                any(Item.class), any(User.class), any(LocalDateTime.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);
        assertEquals(commentDto, itemService.createComment(commentDto, itemId, userId));
    }

    @Test
    public void saveCommentErrorsTest() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L, userId = 1L, bookingId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1), end = LocalDateTime.now().plusDays(1);
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "description of item", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.createComment(commentDto, itemId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого создается комментарий, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of());
        thrown = catchThrowable(() -> {
            itemService.createComment(commentDto, itemId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Вещи, к которой создается комментарий, не существует", thrown.getMessage());

        when(itemRepository.findAll()).thenReturn(List.of(item));
        CommentDto noTextCommentDto = new CommentDto(commentId, "", userName, created);
        thrown = catchThrowable(() -> {
            itemService.createComment(noTextCommentDto, itemId, userId);
        });
        assertThat(thrown).isInstanceOf(WrongParametersException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Поле текста комментария не  может быть пустым", thrown.getMessage());

        User user2 = new User(2L, "Dog", "Wrong");
        Booking booking = new Booking(bookingId, start, end, item, user2, StatusOfBooking.WAITING);
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        thrown = catchThrowable(() -> {
            itemService.createComment(commentDto, itemId, userId);
        });
        assertThat(thrown).isInstanceOf(WrongParametersException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Написать отзыв может только человек, бронировавший вещь!", thrown.getMessage());
    }
}
