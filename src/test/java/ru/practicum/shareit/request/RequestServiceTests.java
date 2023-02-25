package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundAnythingException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
    @Autowired
    ObjectMapper mapper;

    @InjectMocks
    RequestServiceImpl requestService;

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserService userService;

    @Mock
    RequestMapper requestMapper;

    @Test
    public void saveTest() {
        Long userId = 1L;
        ItemRequest request = new ItemRequest("Description");
        request.setId(userId);
        ItemRequestDto requestDto = new ItemRequestDto("Description");
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(requestMapper.toItemRequest(any(ItemRequestDto.class))).thenReturn(request);

        assertEquals(request, requestService.create(requestDto, userId));
    }

    @Test
    public void saveNoUserTest() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto("Description");
        when(userService.userExistById(any(Long.class))).thenReturn(false);

        Throwable thrown = catchThrowable(() -> {
            requestService.create(requestDto, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого происходит поиск запросов, не существует", thrown.getMessage());
    }

    @Test
    void findAllByOwnerTest() {
        Long userId = 1L;
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            requestService.findAllByOwnerWithResponses(userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого происходит поиск запросов, не существует", thrown.getMessage());

        List<ItemRequestDto> requestDtos =
                List.of(new ItemRequestDto("desc"), new ItemRequestDto("description"));
        List<ItemRequest> requests =
                List.of(new ItemRequest("desc"), new ItemRequest("description"));

        when(requestRepository.findAllByRequestorId(userId)).thenReturn(requests);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(requestMapper.toListRequestDto(requests)).thenReturn(requestDtos);

        assertEquals(requestDtos, requestService.findAllByOwnerWithResponses(userId));
    }

    @Test
    void findAll() {
        Long userId = 1L;
        Integer from = 0, size = 1;
        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            requestService.findAll(from, size, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого происходит поиск запросов, не существует", thrown.getMessage());

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        List<ItemRequestDto> requestDtos = List.of(itemRequestDto);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<ItemRequest> page = Page.empty();
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId, pageable)).thenReturn(page);
        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(requestMapper.toListRequestDto(page.getContent())).thenReturn(requestDtos);

        assertEquals(requestService.findAll(from, size, userId), requestDtos);
    }

    @Test
    public void findByIdWithResponsesTest() {
        Long userId = 1L, requestId = 1L;

        when(userService.userExistById(any(Long.class))).thenReturn(false);
        Throwable thrown = catchThrowable(() -> {
            requestService.findByIdWithResponses(requestId, userId);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Пользователя, от лица которого происходит поиск запросов, не существует", thrown.getMessage());

        ItemRequest request = new ItemRequest("Description");
        request.setId(userId);
        ItemRequestDto requestDto = new ItemRequestDto("Description");

        when(userService.userExistById(any(Long.class))).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestMapper.toRequestDto(any(ItemRequest.class))).thenReturn(requestDto);
        assertEquals(requestDto, requestService.findByIdWithResponses(requestId, userId));
    }

    @Test
    public void findByIdTest() {
        Long userId = 1L, requestId = 1L;
        ItemRequest request = new ItemRequest("Description");
        request.setId(userId);

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        assertEquals(request, requestService.findById(requestId));
    }

    @Test
    public void updateTest() {
        Long userId = 1L;
        ItemRequest request = new ItemRequest("Description");
        request.setId(userId);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(requestRepository.findAll()).thenReturn(List.of(request));

        assertEquals(request, requestService.updateItemRequest(request));
    }

    @Test
    public void updateNotExistTest() {
        Long userId = 1L;
        ItemRequest request = new ItemRequest("Description");
        request.setId(userId);
        when(requestRepository.findAll()).thenReturn(List.of());

        Throwable thrown = catchThrowable(() -> {
            requestService.updateItemRequest(request);
        });
        assertThat(thrown).isInstanceOf(NotFoundAnythingException.class);
        assertThat(thrown.getMessage()).isNotBlank();
        assertEquals("Такого запроса не существует", thrown.getMessage());
    }

    @Test
    public void deleteTest() {
        Long userId = 1L;
        requestService.deleteById(userId);

        verify(requestRepository).deleteById(userId);
    }
}