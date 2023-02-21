package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsComments;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    ItemMapper itemMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void findAllByUser() throws Exception {
        Integer from = 0, size = 1;
        Long itemId = 1L, userId = 1L;
        ItemDtoBookingsComments itemDto =
                new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        List<ItemDtoBookingsComments> itemDtos = List.of(itemDto);
        when(itemService.findAllByUser(from, size, userId)).thenReturn(itemDtos);

        String result = mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, Mockito.times(1)).findAllByUser(from, size, userId);
        assertEquals(result, mapper.writeValueAsString(itemDtos));
    }

    @Test
    void findAllByText() throws Exception {
        Integer from = 0, size = 1;
        Long itemId = 1L, userId = 1L;
        String text = "DeScr";
        List<ItemDtoBookingsComments> itemDtos =
                List.of(new ItemDtoBookingsComments(itemId, "Name", "Some description", true));
        when(itemService.findAllByText(from, size, text, userId)).thenReturn(itemDtos);

        String result = mvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "1")
                        .param("text", "DeScr")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, Mockito.times(1)).findAllByText(from, size, text, userId);
        assertEquals(result, mapper.writeValueAsString(itemDtos));
    }

    @Test
    void findById() throws Exception {
        Long itemId = 1L, userId = 1L;
        ItemDtoBookingsComments itemDto =
                new ItemDtoBookingsComments(itemId, "Name", "Some description", true);
        when(itemService.findDtoById(itemId, userId)).thenReturn(itemDto);

        String result = mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, Mockito.times(1)).findDtoById(itemId, userId);
        assertEquals(result, mapper.writeValueAsString(itemDto));
    }

    @Test
    void createItem() throws Exception {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "Name", "Some description", true);

        when(itemService.createItem(itemDto, userId)).thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, Mockito.times(1)).createItem(itemDto, userId);
        assertEquals(mapper.writeValueAsString(itemDto), result);
    }

    @Test
    void updateItemValid() throws Exception {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto =
                new ItemDto(itemId, "Name", "Some description", true);


        when(itemService.updateItem(itemId, itemDto, userId)).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService, Mockito.times(1)).updateItem(itemId, itemDto, userId);
    }

    @Test
    void delete() throws Exception {
        Long itemId = 1L, userId = 1L;
        ItemDto itemDto =
                new ItemDto(itemId, "Name", "Some description", true);

        mvc.perform(MockMvcRequestBuilders.delete("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, Mockito.times(1)).deleteItem(itemId, userId);
    }

    @Test
    void createComment() throws Exception {
        Long itemId = 1L, userId = 1L;
        CommentDto commentDto = new CommentDto(itemId, "Comment text", "Name", LocalDateTime.now());

        when(itemService.createComment(commentDto, itemId, userId)).thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, Mockito.times(1)).createComment(commentDto, itemId, userId);
        assertEquals(mapper.writeValueAsString(commentDto), result);
    }
}
