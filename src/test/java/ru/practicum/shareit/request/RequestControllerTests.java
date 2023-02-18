package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTests {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @MockBean
    RequestMapper requestMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void createRequest() throws Exception {
        ItemRequest request = new ItemRequest("Some description");
        Long requestId = 1L, userId = 1L;
        request.setId(requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, "Some description", LocalDateTime.now());

        when(requestService.create(itemRequestDto, userId)).thenReturn(request);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));

        verify(requestService, Mockito.times(1)).create(itemRequestDto, userId);
    }

    @Test
    void createRequestFutureCreated() throws Exception {
        ItemRequest request = new ItemRequest("Some description");
        Long requestId = 1L, userId = 1L;
        request.setId(requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, "Some description", LocalDateTime.now().plusDays(1));

        when(requestService.create(itemRequestDto, userId)).thenReturn(request);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).create(itemRequestDto, userId);
    }

    @Test
    void findAllOne() throws Exception {
        Long userId = 1L;
        Integer from = 0, size = 1;
        List<ItemRequestDto> requestDtos = List.of(new ItemRequestDto());

        when(requestService.findAll(from, size, userId)).thenReturn(requestDtos);

        String result = mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, Mockito.times(1)).findAll(from, size, userId);
        assertEquals(result, mapper.writeValueAsString(requestDtos));
    }

    @Test
    void findAllByOwnerWithResponses() throws Exception {
        Long userId = 1L;
        List<ItemRequestDto> requestDtos = List.of(new ItemRequestDto());

        when(requestService.findAllByOwnerWithResponses(userId)).thenReturn(requestDtos);

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, Mockito.times(1)).findAllByOwnerWithResponses(userId);
        assertEquals(result, mapper.writeValueAsString(requestDtos));
    }

    @Test
    void findRequestById() throws Exception {
        ItemRequest request = new ItemRequest("Some description");
        Long requestId = 1L, userId = 1L;
        request.setId(requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, "Some description", LocalDateTime.now());

        when(requestService.findByIdWithResponses(requestId, userId)).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));

        verify(requestService, Mockito.times(1)).findByIdWithResponses(requestId, userId);
    }
}
