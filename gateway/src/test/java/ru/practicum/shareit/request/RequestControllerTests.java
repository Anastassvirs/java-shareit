package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTests {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestClient requestClient;

    @Autowired
    private MockMvc mvc;

    private Long requestId;
    private Long userId;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        requestId = 1L;
        userId = 1L;
        itemRequestDto = new ItemRequestDto(requestId, "Some description", LocalDateTime.now());
    }

    @Test
    void createRequestFutureCreated() throws Exception {
        itemRequestDto = new ItemRequestDto(requestId, "Some description", LocalDateTime.now().plusDays(1));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).create(itemRequestDto, userId);
    }
}
