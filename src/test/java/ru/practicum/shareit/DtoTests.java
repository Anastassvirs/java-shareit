package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class DtoTests {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = new UserDto(
                "Anastasia",
                "anastasia.svir@mail.com");
        JsonContent<UserDto> jsonDto = jsonUserDto.write(userDto);

        assertThat(jsonDto).extractingJsonPathStringValue("$.name").isEqualTo("Anastasia");
        assertThat(jsonDto).extractingJsonPathStringValue("$.email").isEqualTo("anastasia.svir@mail.com");
    }

    @Test
    void testBookingDto() throws Exception {
        Long bookingId = 1L, bookerId = 1L, itemId = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 1, 18, 18, 18), end = LocalDateTime.of(2023, 1, 18, 18, 18).plusDays(1);
        BookingDto bookingDto = new BookingDto(bookingId, start, end, bookerId, itemId);
        JsonContent<BookingDto> jsonDto = jsonBookingDto.write(bookingDto);

        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.start").isEqualTo("2023-01-18T18:18:00");
        assertThat(jsonDto).extractingJsonPathStringValue("$.end").isEqualTo("2023-01-19T18:18:00");
        assertThat(jsonDto).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testRequestDto() throws Exception {
        Boolean avaliable = true;
        Long requestId = 1L, itemId = 1L;
        String description = "description", nameItem = "Name", descriptionItem = "description of item";
        LocalDateTime created = LocalDateTime.of(2023, 1, 18, 18, 18);
        ItemDto itemDto = new ItemDto(itemId, nameItem, descriptionItem, avaliable, requestId);
        ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, description, created, List.of(itemDto));
        JsonContent<ItemRequestDto> jsonDto = jsonItemRequestDto.write(itemRequestDto);

        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonDto).extractingJsonPathStringValue("$.created").isEqualTo("2023-01-18T18:18:00");

        assertThat(jsonDto).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(jsonDto).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Name");
        assertThat(jsonDto).extractingJsonPathStringValue("$.items[0].description").isEqualTo("description of item");
        assertThat(jsonDto).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
    }
}
