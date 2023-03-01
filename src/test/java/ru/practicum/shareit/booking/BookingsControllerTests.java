package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingsControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @MockBean
    BookingMapper bookingMapper;

    @Autowired
    MockMvc mvc;

    @Test
    void createTest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        CreateBookingDto bookingDto =
                new CreateBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemId);
        Booking booking =
                new Booking(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item);
        when(bookingService.create(bookingDto, userId)).thenReturn(booking);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, Mockito.times(1)).create(bookingDto, userId);
        assertEquals(mapper.writeValueAsString(booking), result);
    }

    @Test
    void approveTest() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        Long itemId = 1L;
        Item item = new Item();
        item.setId(itemId);
        Booking booking =
                new Booking(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), item);
        when(bookingService.changeStatus(bookingId, userId, Boolean.TRUE)).thenReturn(booking);

        String result = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(Boolean.TRUE))
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, Mockito.times(1)).changeStatus(bookingId, userId, Boolean.TRUE);
        assertEquals(mapper.writeValueAsString(booking), result);
    }

    @Test
    void findTest() throws Exception {
        Long bookingId = 1L;
        Long userId = 1L;
        Item item = new Item();
        Booking booking =
                new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item);
        when(bookingService.findById(bookingId, userId)).thenReturn(booking);

        String result = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, Mockito.times(1)).findById(bookingId, userId);
        assertEquals(result, mapper.writeValueAsString(booking));
    }

    @Test
    void findAllByUser() throws Exception {
        Integer from = 0;
        Integer size = 1;
        Long bookingId = 1L;
        Long userId = 1L;
        Item item = new Item();
        Booking booking =
                new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item);
        List<Booking> bookings = List.of(booking);
        when(bookingService.findAllByUser(from, size, userId, State.ALL)).thenReturn(bookings);

        String result = mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, Mockito.times(1)).findAllByUser(from, size, userId, State.ALL);
        assertEquals(result, mapper.writeValueAsString(bookings));
    }

    @Test
    void findAllByOwner() throws Exception {
        Integer from = 0;
        Integer size = 1;
        Long bookingId = 1L;
        Long userId = 1L;
        Item item = new Item();
        Booking booking =
                new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item);
        List<Booking> bookings = List.of(booking);
        when(bookingService.findAllByOwner(from, size, userId, State.ALL)).thenReturn(bookings);

        String result = mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookings))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, Mockito.times(1)).findAllByOwner(from, size, userId, State.ALL);
        assertEquals(result, mapper.writeValueAsString(bookings));
    }
}
