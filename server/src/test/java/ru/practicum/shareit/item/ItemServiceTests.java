package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;

    private Long userId;
    private Long itemId;
    private Long bookingId;
    private Long bookerId;
    private Long commentId;
    private String text;
    private String userName;
    private Item item;
    private ItemDto shortItemDto;
    private ItemDtoBookingsComments itemDto;
    private User user;
    private BookingDto shortBookingDto;
    private Booking booking;
    private Comment comment;
    private CommentDto commentDto;
    private Integer from;
    private Integer size;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime created;
    private List<Item> items;
    private List<ItemDtoBookingsComments> itemDtos;
    private List<Booking> bookings;
    private List<Comment> comments;
    private Pageable pageable;

    @BeforeEach
    void init() {
        Boolean avaliable = true;
        userId = 1L;
        itemId = 1L;
        bookingId = 1L;
        bookerId = 2L;
        Long requestId = 1L;
        Long requestorId = 1L;
        commentId = 1L;
        from = 0;
        size = 1;
        text = "DeScr";
        String nameItem = "Name";
        userName = "Anastasia";
        String descriptionItem = "Some description";
        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now().plusDays(1);
        created = LocalDateTime.of(2023, 1, 18, 18, 18);
        user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, "description", user, created);
        item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        shortItemDto = new ItemDto(itemId, nameItem, descriptionItem, true, 2L);
        shortBookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        booking = new Booking(bookerId, start, end, item,
                new User(bookerId, "name", "email@gmail.com"), StatusOfBooking.WAITING);
        comment = new Comment(commentId, "comment text", item, user, created);
        commentDto = new CommentDto(commentId, "comment text", userName, created);
        itemDto = new ItemDtoBookingsComments(itemId, nameItem, descriptionItem, true, requestId);
        itemDtos = List.of(itemDto);
        items = List.of(item);
        bookings = List.of(booking);
        comments = List.of(comment);
        pageable = PageRequest.of(from / size, size);
    }

    @Test
    public void findAllTest() {
        when(itemRepository.findAll()).thenReturn(List.of());
        assertEquals(List.of(), itemService.findAll());
    }

    @Test
    public void findAllByUserTest() {
        when(itemRepository.findAllByOwnerIdOrderById(userId, pageable)).thenReturn(items);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterAndStatus(any(Long.class),
                any(LocalDateTime.class), any(StatusOfBooking.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        assertEquals(itemService.findAllByUser(from, size, userId), itemDtos);
    }

    @Test
    public void findAllByUserErrorTest() {
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
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterAndStatus(any(Long.class),
                any(LocalDateTime.class), any(StatusOfBooking.class))).thenReturn(bookings);
        when(bookingRepository.findAllByItemIdAndStartBeforeAndStatus(any(Long.class),
                any(LocalDateTime.class), any(StatusOfBooking.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(itemRepository.findAllByText(text, pageable)).thenReturn(items);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        assertEquals(itemService.findAllByText(from, size, text, userId), itemDtos);
    }

    @Test
    public void findAllByTextErrorsTest() {
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
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(itemService.findDtoById(itemId, userId), itemDto);
    }

    @Test
    public void findDtoByIdErrorTest() {
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
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(itemService.findById(itemId), item);
    }

    @Test
    public void saveTest() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(shortItemDto);
        when(itemMapper.toItem(any(ItemDto.class))).thenReturn(item);

        assertEquals(shortItemDto, itemService.createItem(shortItemDto, userId));
    }

    @Test
    public void saveNoUserTest() {
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.createItem(shortItemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится создание вещи, не существует", thrown.getMessage());
    }

    @Test
    public void updateTest() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(shortItemDto);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertEquals(shortItemDto, itemService.updateItem(itemId, shortItemDto, userId));
    }

    @Test
    public void updateErrorsTest() {
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, shortItemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого производится изменение вещи, не существует", thrown.getMessage());

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of());
        thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, shortItemDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Вещи, которую вы пытаетесь изменить, не существует", thrown.getMessage());

        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        thrown = catchThrowable(() -> {
            itemService.updateItem(itemId, shortItemDto, userId + 1L);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("ID пользователя не соответсвует владельцу вещи", thrown.getMessage());

    }

    @Test
    public void deleteTest() {
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId, userId);
        verify(itemRepository).deleteById(itemId);
    }

    @Test
    public void deleteErrorsTest() {
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
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
        when(userService.findById(any(Long.class))).thenReturn(user);
        when(bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(any(Long.class),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(commentMapper.newtoComment(any(CommentDto.class),
                any(Item.class), any(User.class), any(LocalDateTime.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);
        assertEquals(commentDto, itemService.createComment(commentDto, itemId, bookerId));
    }

    @Test
    public void saveCommentErrorsTest() {
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
