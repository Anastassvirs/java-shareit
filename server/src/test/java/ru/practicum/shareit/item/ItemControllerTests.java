package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.controller.ServiceItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ServiceItemController.class)
public class ItemControllerTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private Long userId;
    private Long itemId;
    private Long requestId;
    private Integer from;
    private Integer size;
    private String text;
    private LocalDateTime created;
    private Item item;
    private ItemDtoBookingsComments itemDto;
    private ItemDto simpleItemDto;
    private List<ItemDtoBookingsComments> itemDtos;
    private User user;
    private ItemRequest itemRequest;
    CommentDto commentDto;

    @BeforeEach
    void init() {
        userId = 1L;
        itemId = 1L;
        requestId = 1L;
        from = 0;
        size = 1;
        text = "DeScr";
        created = LocalDateTime.now().minusDays(1);
        user = new User(userId, "Anastasia", "anastasia.svir@mail.com");
        itemRequest = new ItemRequest(requestId, "desc", user, created.minusDays(1));
        item = new Item(itemId, "itemName", "descdesc", true, user, itemRequest);
        itemDto = new ItemDtoBookingsComments(itemId, "Name", "Some description", true, requestId);
        simpleItemDto = new ItemDto(itemId, "Name", "Some description", true, 2L);
        itemDtos = List.of(itemDto);
        commentDto = new CommentDto(itemId, "Comment text", "Name", LocalDateTime.now());
    }

    @Test
    void findAllByUser() throws Exception {
        when(itemService.findAllByUser(from, size, userId)).thenReturn(itemDtos);

        String result = mvc.perform(get("/items").param("from", "0").param("size", "1").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemDtos)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService, Mockito.times(1)).findAllByUser(from, size, userId);
        assertEquals(result, mapper.writeValueAsString(itemDtos));
    }

    @Test
    void findAllByText() throws Exception {
        when(itemService.findAllByText(from, size, text, userId)).thenReturn(itemDtos);

        String result = mvc.perform(get("/items/search").param("from", "0").param("size", "1").param("text", "DeScr").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemDtos)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService, Mockito.times(1)).findAllByText(from, size, text, userId);
        assertEquals(result, mapper.writeValueAsString(itemDtos));
    }

    @Test
    void findById() throws Exception {
        when(itemService.findDtoById(itemId, userId)).thenReturn(itemDto);

        String result = mvc.perform(get("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(itemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService, Mockito.times(1)).findDtoById(itemId, userId);
        assertEquals(result, mapper.writeValueAsString(itemDto));
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(simpleItemDto, userId)).thenReturn(simpleItemDto);

        String result = mvc.perform(post("/items").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(simpleItemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService, Mockito.times(1)).createItem(simpleItemDto, userId);
        assertEquals(mapper.writeValueAsString(simpleItemDto), result);
    }

    @Test
    void updateItemValid() throws Exception {
        when(itemService.updateItem(itemId, simpleItemDto, userId)).thenReturn(simpleItemDto);

        mvc.perform(patch("/items/{itemId}", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(simpleItemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(simpleItemDto.getId()), Long.class)).andExpect(jsonPath("$.name", is(simpleItemDto.getName()))).andExpect(jsonPath("$.description", is(simpleItemDto.getDescription()))).andExpect(jsonPath("$.available", is(simpleItemDto.getAvailable())));

        verify(itemService, Mockito.times(1)).updateItem(itemId, simpleItemDto, userId);
    }

    @Test
    void delete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/items").header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(simpleItemDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(itemService, Mockito.times(1)).deleteItem(itemId, userId);
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(commentDto, itemId, userId)).thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId).header("X-Sharer-User-Id", userId).content(mapper.writeValueAsString(commentDto)).characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        verify(itemService, Mockito.times(1)).createComment(commentDto, itemId, userId);
        assertEquals(mapper.writeValueAsString(commentDto), result);
    }
}
