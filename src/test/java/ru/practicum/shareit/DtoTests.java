package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusOfBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@JsonTest
public class DtoTests {

    @InjectMocks
    private UserMapper userMapper;

    @InjectMocks
    private BookingMapper bookingMapper;

    @InjectMocks
    private RequestMapper requestMapper;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemMapper itemMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapperMock;

    @InjectMocks
    private CommentMapper commentMapper;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    private Boolean avaliable;
    private Long requestId;
    private Long itemId;
    private Long requestorId;
    private Long commentId;
    private String nameItem;
    private String descriptionItem;
    private String description;
    private String userName;
    private Long bookingId;
    private Long bookerId;
    private String commentText;
    private LocalDateTime created;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user;
    private UserDto userDto;
    private ItemRequest itemRequest;
    private ItemRequest itemRequestCreated;
    private Item item;
    private ItemDto itemDto;
    private List<ItemDto> itemDtos;
    private List<Item> items;
    private ItemDtoBookingsComments itemDtoBookingsComments;
    private ItemRequestDto itemRequestDto;
    private Comment comment;
    private CommentDto commentDto;


    @BeforeEach
    void init() {
        avaliable = true;
        requestId = 1L;
        itemId = 1L;
        requestorId = 1L;
        commentId = 1L;
        bookingId = 1L;
        bookerId = 1L;
        description = "description";
        nameItem = "Name";
        descriptionItem = "description of item";
        userName = "Anastasia";
        commentText = "comment text";
        created = LocalDateTime.of(2023, 1, 18, 18, 18);
        start = LocalDateTime.of(2023, 1, 18, 18, 18);
        end = LocalDateTime.of(2023, 1, 18, 18, 18).plusDays(1);
        user = new User(requestorId, "Anastasia", "anastasia.svir@mail.com");
        userDto = new UserDto("Anastasia", "anastasia.svir@mail.com");
        itemDto = new ItemDto(itemId, nameItem, descriptionItem, avaliable, requestId);
        itemDtos = List.of(itemDto);
        itemRequest = new ItemRequest(requestId, description, user, created);
        itemRequestDto = new ItemRequestDto(requestId, description, created, itemDtos);
        itemRequestCreated = new ItemRequest(description);
        item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        itemDtoBookingsComments = new ItemDtoBookingsComments(itemId, nameItem, descriptionItem, avaliable, requestId);
        items = List.of(item);
        comment = new Comment(commentId, commentText, item, user, created);
        commentDto = new CommentDto(commentId, commentText, userName, created);
    }

    @Test
    void testUserDto() throws Exception {
        JsonContent<UserDto> jsonDto = jsonUserDto.write(userDto);

        assertThat(jsonDto).extractingJsonPathStringValue("$.name").isEqualTo("Anastasia");
        assertThat(jsonDto).extractingJsonPathStringValue("$.email").isEqualTo("anastasia.svir@mail.com");
        assertEquals(userDto, userMapper.toUserDto(user));
    }

    @Test
    void testItemDto() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertEquals(itemDto, itemMapper.toItemDto(item));
        assertEquals(itemDtoBookingsComments, itemMapper.toItemDtoBookingsComments(item));
        assertEquals(itemDtos, itemMapper.toListItemDto(items));
        item.setOwner(null);
        assertEquals(item, itemMapper.toItem(itemDto));
    }

    @Test
    void testCommentDto() {
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        assertEquals(commentDto, commentMapper.toCommentDto(comment));
        LocalDateTime created2 = LocalDateTime.now();
        comment = new Comment(commentText, item, user, created2);
        assertEquals(comment, commentMapper.newtoComment(commentDto, item, user, created2));
    }

    @Test
    void testBookingDto() throws Exception {
        CreateBookingDto bookingCreationDto = new CreateBookingDto(start, end, itemId);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Booking booking = new Booking(start, end, item);
        assertEquals(booking, bookingMapper.toBookingCreation(bookingCreationDto));
        BookingDto bookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        booking = new Booking(bookerId, start, end, item,
                new User(bookerId, "name", "email@gmail.com"), StatusOfBooking.WAITING);
        JsonContent<BookingDto> jsonDto = jsonBookingDto.write(bookingDto);

        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-18T18:18:00");
        assertThat(jsonDto).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-19T18:18:00");
        assertThat(jsonDto).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertEquals(bookingDto, bookingMapper.toBookingDto(booking));
    }

    @Test
    void testRequestDto() throws Exception {
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto);
        JsonContent<ItemRequestDto> jsonDto = jsonItemRequestDto.write(itemRequestDto);

        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);
        when(itemMapperMock.toListItemDto(items)).thenReturn(List.of(itemDto));

        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonDto).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-18T18:18:00");

        assertThat(jsonDto).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Name");
        assertThat(jsonDto).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description of item");
        assertThat(jsonDto).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
        assertEquals(itemRequestDto, requestMapper.toRequestDto(itemRequest));
        assertEquals(itemRequestDtos, requestMapper.toListRequestDto(List.of(itemRequest)));
        assertEquals(itemRequestCreated, requestMapper.toItemRequest(itemRequestDto));
    }
}
