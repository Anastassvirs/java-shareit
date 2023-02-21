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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void findAllByUserTest() {
        Integer from = 0, size = 1;
        Long itemId = 1L, userId = 1L;
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<ItemDtoBookingsComments> itemDtos = List.of(itemDto);
        Item item = new Item(itemId, "Name", "Some description", true);
        List<Item> items = List.of(item);
        List<Booking> bookings = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size);
        when(itemRepository.findAllByOwnerIdOrderById(userId, pageable)).thenReturn(items);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);

        assertEquals(itemService.findAllByUser(from, size, userId), itemDtos);
    }

    @Test
    public void findAllByTextTest() {
        String text = "DeScr";
        Integer from = 0, size = 1;
        Long itemId = 1L, userId = 1L;
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<ItemDtoBookingsComments> itemDtos = List.of(itemDto);
        Item item = new Item(itemId, "Name", "Some description", true);
        List<Item> items = List.of(item);
        List<Booking> bookings = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();

        Pageable pageable = PageRequest.of(from / size, size);
        when(itemRepository.findAllByText(text, pageable)).thenReturn(items);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(any(Long.class), any(LocalDateTime.class))).thenReturn(bookings);
        when(commentRepository.findAllByItemId(any(Long.class))).thenReturn(comments);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);

        assertEquals(itemService.findAllByText(from, size, text, userId), itemDtos);
    }

    @Test
    public void findDtoByIdTest() {
        Long itemId = 1L, userId = 1L;
        ItemDtoBookingsComments itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        Item item = new Item(itemId, "Name", "Some description", true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(itemMapper.toItemDtoBookingsComments(item)).thenReturn(itemDto);

        assertEquals(itemService.findDtoById(itemId, userId), itemDto);
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

}
