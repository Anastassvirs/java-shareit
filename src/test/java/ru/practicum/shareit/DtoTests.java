package ru.practicum.shareit;

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
    UserMapper userMapper;

    @InjectMocks
    BookingMapper bookingMapper;

    @InjectMocks
    RequestMapper requestMapper;

    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    ItemMapper itemMapper;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapperMock;

    @InjectMocks
    CommentMapper commentMapper;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    void testUserDto() throws Exception {
        User user = new User("Anastasia",
                "anastasia.svir@mail.com");

        UserDto userDto = new UserDto(
                "Anastasia",
                "anastasia.svir@mail.com");
        JsonContent<UserDto> jsonDto = jsonUserDto.write(userDto);

        assertThat(jsonDto).extractingJsonPathStringValue("$.name").isEqualTo("Anastasia");
        assertThat(jsonDto).extractingJsonPathStringValue("$.email").isEqualTo("anastasia.svir@mail.com");
        assertEquals(userDto, userMapper.toUserDto(user));
    }

    @Test
    void testItemDto() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L;
        String description = "description", nameItem = "Name", descriptionItem = "description of item";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        ItemDto itemDto = new ItemDto(itemId, nameItem, descriptionItem, avaliable, requestId);
        ItemDtoBookingsComments itemDtoBookingsComments = new ItemDtoBookingsComments(itemId, nameItem, descriptionItem, avaliable, requestId);
        User user = new User(requestorId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        List<Item> items = List.of(item);
        List<ItemDto> itemDtos = List.of(itemDto);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertEquals(itemDto, itemMapper.toItemDto(item));
        assertEquals(itemDtoBookingsComments, itemMapper.toItemDtoBookingsComments(item));
        assertEquals(itemDtos, itemMapper.toListItemDto(items));
        item = new Item(itemId, nameItem, descriptionItem, avaliable, itemRequest);
        assertEquals(item, itemMapper.toItem(itemDto));
    }

    @Test
    void testCommentDto() {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L, commentId = 1L;
        String description = "description", nameItem = "Name", userName = "Anastasia",
                descriptionItem = "description of item", commentText = "comment text";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        User user = new User(requestorId, userName, "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        Comment comment = new Comment(commentId, commentText, item, user, created);
        CommentDto commentDto = new CommentDto(commentId, commentText, userName, created);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        assertEquals(commentDto, commentMapper.toCommentDto(comment));
        comment = new Comment(commentText, item, user, LocalDateTime.now());
        assertEquals(comment, commentMapper.newtoComment(commentDto, item, user));
    }

    @Test
    void testBookingDto() throws Exception {
        Long bookingId = 1L, bookerId = 1L, itemId = 1L;
        Item item = new Item(itemId, "ha", "he", true);
        LocalDateTime start = LocalDateTime.of(2023, 1, 18, 18, 18),
                end = LocalDateTime.of(2023, 1, 18, 18, 18).plusDays(1);
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
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L, requestorId = 1L;
        String description = "description", nameItem = "Name", descriptionItem = "description of item";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        ItemDto itemDto = new ItemDto(itemId, nameItem, descriptionItem, avaliable, requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, description, created, List.of(itemDto));
        User user = new User(requestorId, "Anastasia", "anastasia.svir@mail.com");
        ItemRequest itemRequest = new ItemRequest(requestId, description, user, created);
        ItemRequest itemRequestCreated = new ItemRequest(description);
        Item item = new Item(itemId, nameItem, descriptionItem, avaliable, user, itemRequest);
        List<Item> items = List.of(item);
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
