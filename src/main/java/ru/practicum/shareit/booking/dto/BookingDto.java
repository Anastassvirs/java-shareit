package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull
    private Long id;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @Future
    @NotNull
    private LocalDateTime end;
    private Long bookerId;
    private Long itemId;

    public BookingDto(LocalDateTime start, LocalDateTime end, Long bookerId, Long itemId) {
        this.start = start;
        this.end = end;
        this.bookerId = bookerId;
        this.itemId = itemId;
    }
}
